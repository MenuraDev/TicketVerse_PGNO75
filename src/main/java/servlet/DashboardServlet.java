// src/main/java/servlet/DashboardServlet.java
package servlet;

import model.Movie;
import model.Reservation;
import model.User;
import service.MovieService;        // Import MovieService
import service.ReservationService;  // Import ReservationService
import service.UserService;         // Import UserService (if needed, though user is from session)

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    // Add ReservationService and MovieService
    private ReservationService reservationService;
    private MovieService movieService;
    // private UserService userService; // Might not be needed if only using session user

    @Override
    public void init() throws ServletException {
        super.init();
        String reservationFilePath = getServletContext().getRealPath("/WEB-INF/database/reservations.txt");
        reservationService = new ReservationService(reservationFilePath);

        String movieFilePath = getServletContext().getRealPath("/WEB-INF/database/movies.txt");
        movieService = new MovieService(movieFilePath);

        // String userFilePath = getServletContext().getRealPath("/WEB-INF/database/users.txt");
        // userService = new UserService(userFilePath);
    }

    // Inner class to hold combined Reservation + Movie Title details for the JSP
    public static class ReservationDetail {
        private final Long id;
        private final String movieTitle;
        private final String showtime; // Original showtime string
        private final String seats;    // Pre-joined seat string
        private final Date reservationTime; // java.util.Date for fmt:formatDate
        private final boolean reservationStatus;

        public ReservationDetail(Long id, String movieTitle, String showtime, String seats, Date reservationTime, boolean reservationStatus) {
            this.id = id;
            this.movieTitle = movieTitle;
            this.showtime = showtime;
            this.seats = seats;
            this.reservationTime = reservationTime;
            this.reservationStatus = reservationStatus;
        }

        // --- Getters needed by JSTL/EL ---
        public Long getId() { return id; }
        public String getMovieTitle() { return movieTitle; }
        public String getShowtime() { return showtime; }
        public String getSeats() { return seats; }
        public Date getReservationTime() { return reservationTime; }
        public boolean getReservationStatus() { return reservationStatus; }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if ("admin".equals(user.getRole())) {
            // Admin logic remains the same
            request.setAttribute("dashboardContent", "Welcome, Admin! You have full control.");
            // Forward to admin servlet/JSP - careful not to create redirect loop if admin forwards back here
            // It might be better to redirect admin directly from login or have separate servlet mapping
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response); // Assuming adminDashboard.jsp exists
            // response.sendRedirect("admin?action=adminDashboard"); // Or redirect
        } else {
            // --- Regular User Logic ---
            request.setAttribute("dashboardContent", "Welcome, User! Here are your reservations.");

            try {
                // 1. Get reservations for the current user
                List<Reservation> userReservations = reservationService.getReservationsByUserId(user.getId());

                // 2. Get all movie titles for efficient lookup (avoid reading file repeatedly)
                List<Movie> allMovies = movieService.getAllMovies();
                Map<Long, String> movieTitlesMap = allMovies.stream()
                        .collect(Collectors.toMap(Movie::getId, Movie::getTitle, (t1, t2) -> t1)); // Handle potential duplicate movie IDs if file corrupted

                // 3. Create a list of details for the JSP
                List<ReservationDetail> reservationDetails = new ArrayList<>();
                for (Reservation res : userReservations) {
                    String movieTitle = movieTitlesMap.getOrDefault(res.getMovieId(), "Unknown Movie"); // Get title or default

                    // Format seats
                    String formattedSeats = "N/A";
                    if (res.getSelectedSeats() != null && !res.getSelectedSeats().isEmpty()) {
                        formattedSeats = res.getSelectedSeats().stream().sorted().collect(Collectors.joining(", "));
                    }

                    // Convert LocalDateTime to java.util.Date for JSTL tag
                    Date utilDate = null;
                    if (res.getReservationTime() != null) {
                        utilDate = Date.from(res.getReservationTime().atZone(ZoneId.systemDefault()).toInstant());
                    }

                    reservationDetails.add(new ReservationDetail(
                            res.getId(),
                            movieTitle,
                            res.getShowtime(), // Pass original showtime string
                            formattedSeats,
                            utilDate,
                            res.isReservationStatus()
                    ));
                }

                // Sort details by reservation time (most recent first)
                reservationDetails.sort(Comparator.comparing(ReservationDetail::getReservationTime, Comparator.nullsLast(Comparator.reverseOrder())));


                // 4. Set the prepared list as a request attribute
                request.setAttribute("reservationDetails", reservationDetails);

            } catch (IOException e) {
                // Log the error and inform the user
                getServletContext().log("Error fetching user reservations or movie data", e);
                request.setAttribute("dashboardError", "Could not load your reservation history. Please try again later.");
            }

            // 5. Forward to the user dashboard JSP
            request.getRequestDispatcher("/userDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // 1. Check login
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // Only handle POST for users (admins might have other POST actions elsewhere)
        if (!"admin".equals(user.getRole())) {
            String action = request.getParameter("action");

            if ("cancelReservation".equals(action)) {
                String reservationIdParam = request.getParameter("reservationId");
                if (reservationIdParam == null || reservationIdParam.trim().isEmpty()) {
                    session.setAttribute("cancelError", "Invalid request: Missing reservation ID.");
                    response.sendRedirect("dashboard"); // Redirect back
                    return;
                }

                try {
                    long reservationId = Long.parseLong(reservationIdParam);

                    // *** Security Check: Verify ownership ***
                    Reservation reservationToCancel = reservationService.getReservationById(reservationId);

                    if (reservationToCancel == null) {
                        session.setAttribute("cancelError", "Reservation not found.");
                    } else if (!reservationToCancel.getUserId().equals(user.getId())) {
                        // Log this attempt - potential security issue or bug
                        getServletContext().log("SECURITY ALERT: User " + user.getUsername() + " (ID: " + user.getId() +
                                ") attempted to cancel reservation ID " + reservationId +
                                " owned by user ID " + reservationToCancel.getUserId());
                        session.setAttribute("cancelError", "You can only cancel your own reservations.");
                    } else {
                        // Ownership verified, proceed with deletion
                        reservationService.deleteReservation(reservationId);
                        session.setAttribute("cancelSuccess", "Reservation successfully cancelled.");
                    }

                } catch (NumberFormatException e) {
                    session.setAttribute("cancelError", "Invalid reservation ID format.");
                } catch (IOException e) {
                    getServletContext().log("Error cancelling reservation ID " + reservationIdParam, e);
                    session.setAttribute("cancelError", "Could not cancel reservation due to a server error.");
                }
            } else {
                // Handle other potential user POST actions here if needed
                session.setAttribute("cancelError", "Invalid action specified.");
            }
        } else {
            // Handle admin POST actions if this servlet were responsible for them
            // Or redirect admin elsewhere
            response.sendRedirect("admin"); // Example redirect for admin POSTs here
            return; // Prevent further processing for admin
        }

        // Redirect back to the dashboard after processing the action (important!)
        response.sendRedirect("dashboard");
    }
}

