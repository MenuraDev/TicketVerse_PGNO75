// src/main/java/servlet/BookingServlet.java
package servlet;

import model.Movie;
import model.Reservation;
import model.User;
import service.MovieService;
import service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/book-tickets") // Match the URL from movieDetails.jsp
public class BookingServlet extends HttpServlet {

  private MovieService movieService;
  private ReservationService reservationService;

  @Override
  public void init() throws ServletException {
    super.init();
    movieService = new MovieService(movieFilePath);
    reservationService = new ReservationService(reservationFilePath);
    // Ensure UserService is initialized if needed later, or get user from session
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    // 1. Check if user is logged in
    if (session == null || session.getAttribute("user") == null) {
      // Store the intended destination to redirect after login
      String queryString = request.getQueryString();
      String redirectUrl = request.getRequestURI() + (queryString != null ? "?" + queryString : "");
      response.sendRedirect("login.jsp?redirect=" + java.net.URLEncoder.encode(redirectUrl, "UTF-8"));
      return;
    }

    // 2. Get parameters
    String movieIdParam = request.getParameter("movieId");
    String showtime = request.getParameter("showtime"); // The specific date/time string
    String errorMessage = null;
    Movie movie = null;
    Set<String> bookedSeats = new HashSet<>(); // Initialize empty

    // 3. Validate parameters
    if (movieIdParam == null || movieIdParam.isEmpty() || showtime == null || showtime.isEmpty()) {
      errorMessage = "Movie ID and Showtime are required.";
    } else {
      try {
        long movieId = Long.parseLong(movieIdParam);
        movie = movieService.getMovieById(movieId);

        if (movie == null) {
          errorMessage = "Movie not found.";
        } else {
          // 4. Fetch already booked seats for this specific showtime
          bookedSeats = reservationService.getBookedSeats(movieId, showtime);
        }

      } catch (NumberFormatException e) {
        errorMessage = "Invalid Movie ID format.";
      } catch (IOException e) {
        errorMessage = "Error retrieving booking information: " + e.getMessage();
        // Log the error
        getServletContext().log("Error in BookingServlet doGet", e);
      }
    }

    // 5. Set attributes and forward to JSP
    if (errorMessage != null) {
      request.setAttribute("errorMessage", errorMessage);
      // Forward to an error page or back to movie details?
      // Forwarding back to details might be better if movie wasn't found
      if (movie == null && movieIdParam != null) {
        response.sendRedirect("movie-details?id=" + movieIdParam + "&error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        return;
      }
      // If movie exists but other error, show on booking page
      request.setAttribute("movie", movie); // Still might need movie info
      request.setAttribute("showtime", showtime);
    }

    request.setAttribute("movie", movie);
    request.setAttribute("showtime", showtime);
    request.setAttribute("bookedSeats", bookedSeats); // Pass the set of booked seats
    request.getRequestDispatcher("/bookTickets.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    // 1. Check login status
    if (session == null || session.getAttribute("user") == null) {
      response.sendRedirect("login.jsp"); // Or show an error
      return;
    }
    User user = (User) session.getAttribute("user");

    // 2. Get parameters from form submission
    String movieIdParam = request.getParameter("movieId");
    String showtime = request.getParameter("showtime");
    String[] selectedSeatsArray = request.getParameterValues("selectedSeats"); // Get the array of selected seats

    Movie movie = null;
    long movieId = -1;

    // 3. Basic Validation
    if (movieIdParam == null || showtime == null || selectedSeatsArray == null || selectedSeatsArray.length == 0) {
      request.setAttribute("errorMessage", "Missing booking information or no seats selected.");
      // Need to fetch movie and booked seats again to re-render the page correctly
      try {
        movieId = Long.parseLong(movieIdParam);
        movie = movieService.getMovieById(movieId);
        Set<String> bookedSeats = reservationService.getBookedSeats(movieId, showtime);
        request.setAttribute("movie", movie);
        request.setAttribute("showtime", showtime);
        request.setAttribute("bookedSeats", bookedSeats);
      } catch (Exception e) {
        // Handle cases where movie ID is invalid on POST somehow
        getServletContext().log("Error fetching data for re-render on POST", e);
        request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
      }
      request.getRequestDispatcher("/bookTickets.jsp").forward(request, response);
      return;
    }

    List<String> selectedSeats = Arrays.asList(selectedSeatsArray);

    try {
      movieId = Long.parseLong(movieIdParam);
      movie = movieService.getMovieById(movieId); // Get movie for context if needed

      if (movie == null) {
        throw new ServletException("Movie not found during booking process.");
      }


      // 4. *** CRUCIAL: Re-check seat availability ***
      // This prevents race conditions where two users try to book the same seat simultaneously.
      Set<String> currentBookedSeats = reservationService.getBookedSeats(movieId, showtime);
      for (String selectedSeat : selectedSeats) {
        if (currentBookedSeats.contains(selectedSeat)) {
          request.setAttribute("errorMessage", "Sorry, seat " + selectedSeat + " was just booked by someone else. Please choose different seats.");
          // Re-fetch booked seats *including* the newly booked one for accurate display
          Set<String> latestBookedSeats = reservationService.getBookedSeats(movieId, showtime);
          request.setAttribute("movie", movie);
          request.setAttribute("showtime", showtime);
          request.setAttribute("bookedSeats", latestBookedSeats); // Use the latest set
          request.getRequestDispatcher("/bookTickets.jsp").forward(request, response);
          return; // Stop processing
        }
      }

      // 5. Create Reservation object
      // For now, beverages are empty. You'd add inputs for them on the JSP later.
      Reservation reservation = new Reservation(
          movieId,
          showtime,
          user.getId(),
          selectedSeats,
          "" // Empty beverages string for now
      );
      reservation.setReservationTime(LocalDateTime.now()); // Explicitly set, though constructor does it

      // 6. Save the reservation
      reservationService.addReservation(reservation);

      // 7. Prepare booking details for display
      String formattedSeats = "None";
      if (reservation.getSelectedSeats() != null && !reservation.getSelectedSeats().isEmpty()) {
        formattedSeats = reservation.getSelectedSeats().stream()
            .sorted()
            .collect(Collectors.joining(", "));
      }

      // Convert LocalDateTime to java.util.Date
      LocalDateTime ldt = reservation.getReservationTime();
      Date utilDate = null;
      if (ldt != null) {
        utilDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
      }

      // Store booking details in session
      session.setAttribute("bookingSuccess", "Your reservation has been successfully made!");
      session.setAttribute("lastBookingMovieId", reservation.getMovieId());
      session.setAttribute("lastBookingShowtime", reservation.getShowtime());
      session.setAttribute("lastBookingSeatsFormatted", formattedSeats);
      session.setAttribute("lastBookingTimestamp", utilDate);

      // Redirect to dashboard
      response.sendRedirect("dashboard");
      return;


    } catch (NumberFormatException e) {
      request.setAttribute("errorMessage", "Invalid movie ID.");
      forwardWithError(request, response, movie, showtime, movieId);
    } catch (IOException e) {
      getServletContext().log("Error saving reservation", e);
      request.setAttribute("errorMessage", "Failed to save booking: " + e.getMessage());
      forwardWithError(request, response, movie, showtime, movieId);
    } catch (Exception e) { // Catch any other unexpected errors
      getServletContext().log("Unexpected error during booking POST", e);
      request.setAttribute("errorMessage", "An unexpected error occurred during booking.");
      forwardWithError(request, response, movie, showtime, movieId);
    }
  }

  // Helper method to forward back to the booking page with error context
  private void forwardWithError(HttpServletRequest request, HttpServletResponse response, Movie movie, String showtime, long movieId) throws ServletException, IOException {
    request.setAttribute("movie", movie); // May be null if movie fetch failed earlier
    request.setAttribute("showtime", showtime);
    try {
      // Try to fetch booked seats again for accurate display, even on error
      if (movieId > 0 && showtime != null) {
        Set<String> bookedSeats = reservationService.getBookedSeats(movieId, showtime);
        request.setAttribute("bookedSeats", bookedSeats);
      } else {
        request.setAttribute("bookedSeats", new HashSet<>());
      }
    } catch (IOException ioEx) {
      getServletContext().log("Error fetching booked seats for error page render", ioEx);
      request.setAttribute("bookedSeats", new HashSet<>()); // Default to empty if fetch fails
    }
    request.getRequestDispatcher("/bookTickets.jsp").forward(request, response);
  }

}