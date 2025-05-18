// src/main/java/servlet/AdminServlet.java
package servlet;

import model.Movie;
import model.Reservation;
import model.User;
import service.MovieService;
import service.ReservationService;
import service.UserService;
import utils.CircularQueue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private UserService userService;
    private MovieService movieService;
    private ReservationService reservationService;
    private CircularQueue reservationQueue;

    // Inner DTO class for displaying reservation details in admin view
    public static class AdminReservationView {
        private final Long reservationId;
        private final String username;
        private final Long userId;
        private final String movieTitle;
        private final String showtime;
        private final String seats;
        private final Date reservationTime; // java.util.Date for JSTL fmt tag
        private final boolean reservationStatus;

        public AdminReservationView(Long reservationId, String username, Long userId, String movieTitle, String showtime, String seats, Date reservationTime, boolean reservationStatus) {
            this.reservationId = reservationId;
            this.username = username;
            this.userId = userId;
            this.movieTitle = movieTitle;
            this.showtime = showtime;
            this.seats = seats;
            this.reservationTime = reservationTime;
            this.reservationStatus = reservationStatus;
        }

        // Getters required for EL access in JSP
        public Long getReservationId() { return reservationId; }
        public String getUsername() { return username; }
        public Long getUserId() { return userId; }
        public String getMovieTitle() { return movieTitle; }
        public String getShowtime() { return showtime; }
        public String getSeats() { return seats; }
        public Date getReservationTime() { return reservationTime; }
        public boolean getReservationStatus() { return reservationStatus; }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize all required services
        try {
            String userFilePath = getServletContext().getRealPath("/WEB-INF/database/users.txt");
            userService = new UserService(userFilePath);
            getServletContext().log("UserService initialized with path: " + userFilePath); // Log path

            String movieFilePath = getServletContext().getRealPath("/WEB-INF/database/movies.txt");
            movieService = new MovieService(movieFilePath);
            getServletContext().log("MovieService initialized with path: " + movieFilePath); // Log path

            String reservationFilePath = getServletContext().getRealPath("/WEB-INF/database/reservations.txt");
            reservationService = new ReservationService(reservationFilePath);
            getServletContext().log("ReservationService initialized with path: " + reservationFilePath); // Log path

            // Initialize the circular queue with a capacity of 100
            reservationQueue = new CircularQueue(100);

        } catch (RuntimeException e) {
            getServletContext().log("FATAL: Error initializing services in AdminServlet", e);
            throw new ServletException("Failed to initialize core services.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // --- Admin Access Check ---
        if (session == null || session.getAttribute("user") == null || !"admin".equals(((User) session.getAttribute("user")).getRole())) {
            // Log unauthorized access attempt?
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "adminDashboard"; // Default action
        }

        try {
            switch (action) {
                // --- User Management (GET) ---
                case "addUser":
                    request.getRequestDispatcher("/addUser.jsp").forward(request, response);
                    break;
                case "editUser":
                    showEditUserForm(request, response);
                    break;
                case "deleteUser":
                    deleteUser(request, response); // Handles redirect internally
                    break;
                case "manageUsers":
                    listUsers(request, response);
                    break;

                // --- Reservation Management (GET) ---
                case "manageReservations":
                    listAllReservations(request, response);
                    break;

                // --- Default Admin View ---
                case "adminDashboard":
                default:
                    request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
                    break;
            }
        } catch (Exception e) { // Catch broader exceptions during GET processing
            getServletContext().log("Error processing GET request in AdminServlet (action=" + action + ")", e);
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
        }

        // Add the queue to the request attributes
        request.setAttribute("reservationQueue", reservationQueue);
        request.setAttribute("queueSize", reservationQueue.size());
        request.setAttribute("queueTickets", reservationQueue.getAllTickets());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // --- Admin Access Check ---
        if (session == null || session.getAttribute("user") == null || !"admin".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String targetRedirect = "admin"; // Default redirect

        try {
            if (action == null) {
                throw new ServletException("Action parameter missing for POST request.");
            }

            switch (action) {
                // --- User Management (POST) ---
                case "saveUser":
                    saveUser(request, response); // Handles redirect/forward internally
                    return; // Prevent default redirect at end
                case "updateUser":
                    updateUser(request, response); // Handles redirect/forward internally
                    return; // Prevent default redirect at end

                // --- Reservation Management (POST) ---
                case "approveAllReservations":
                    try {
                        List<Reservation> allReservations = reservationService.getAllReservations();
                        boolean anyApproved = false;
                        for (Reservation r : allReservations) {
                            if (!r.isReservationStatus()) {
                                r.setReservationStatus(true);
                                anyApproved = true;
                            }
                        }
                        if (anyApproved) {
                            reservationService.saveAllReservations(allReservations);
                        }
                        reservationQueue.clear();
                        request.getSession().setAttribute("adminReservationSuccess", "All pending reservations have been approved and the queue is now empty.");
                        // Instead of redirecting, forward to the same page
                        listAllReservations(request, response);
                        return; // Return to prevent the default redirect at the end
                    } catch (Exception e) {
                        request.getSession().setAttribute("adminReservationError", "Error approving all reservations: " + e.getMessage());
                    }
                    break;
                case "addToQueue":
                    int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                    if (reservationQueue.enqueue(ticketId)) {
                        request.getSession().setAttribute("adminReservationSuccess", "Ticket added to queue successfully!");
                    } else {
                        request.getSession().setAttribute("adminReservationError", "Queue is full!");
                    }
                    break;
                case "removeFromQueue":
                    int removedTicketId = reservationQueue.dequeue();
                    if (removedTicketId != -1) {
                        try {
                            Reservation reservation = reservationService.getReservationById((long) removedTicketId);
                            if (reservation != null) {
                                reservation.setReservationStatus(true);
                                // Update the reservation in the file
                                List<Reservation> allReservations = reservationService.getAllReservations();
                                for (Reservation r : allReservations) {
                                    if (r.getId().equals(reservation.getId())) {
                                        r.setReservationStatus(true);
                                        break;
                                    }
                                }
                                reservationService.saveAllReservations(allReservations);
                                request.getSession().setAttribute("adminReservationSuccess", "Ticket " + removedTicketId + " approved and removed from queue!");
                                // Instead of redirecting, forward to the same page
                                listAllReservations(request, response);
                                return; // Return to prevent the default redirect at the end
                            } else {
                                request.getSession().setAttribute("adminReservationError", "Reservation not found for ticket " + removedTicketId + ".");
                            }
                        } catch (Exception e) {
                            request.getSession().setAttribute("adminReservationError", "Error approving reservation: " + e.getMessage());
                        }
                    } else {
                        request.getSession().setAttribute("adminReservationError", "Queue is empty!");
                    }
                    break;

                default:
                    getServletContext().log("AdminServlet received unknown POST action: " + action);
                    request.getSession().setAttribute("adminError", "Unknown action requested.");
                    break; // Fall through to default redirect
            }
        } catch (Exception e) { // Catch broader exceptions during POST processing
            getServletContext().log("Error processing POST request in AdminServlet (action=" + action + ")", e);
            // Set error message in session as we will redirect
            request.getSession().setAttribute("adminError", "An unexpected error occurred processing your request.");
            // Determine where to redirect based on error context if possible, otherwise default
            if ("saveUser".equals(action)) targetRedirect = "admin?action=addUser";
            else if ("updateUser".equals(action)) {
                String id = request.getParameter("id");
                targetRedirect = (id != null) ? "admin?action=editUser&id="+id : "admin?action=manageUsers";
            }
        }
        // Redirect after POST action (unless already handled)
        response.sendRedirect(targetRedirect);
    }

    // =========================================================================
    // Helper Methods for User Management
    // =========================================================================

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<User> users = userService.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/manageUsers.jsp").forward(request, response);
    }

    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Long userId = Long.parseLong(request.getParameter("id"));
            User userToEdit = findUserById(userId);
            if (userToEdit == null) {
                request.getSession().setAttribute("adminUserError", "User with ID " + userId + " not found.");
                response.sendRedirect("admin?action=manageUsers");
                return;
            }
            request.setAttribute("userToEdit", userToEdit);
            request.getRequestDispatcher("/editUser.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("adminUserError", "Invalid user ID format provided.");
            response.sendRedirect("admin?action=manageUsers");
        }
    }

    private void saveUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String role = request.getParameter("role");

        // Basic validation
        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match or are missing.");
            request.getRequestDispatcher("/addUser.jsp").forward(request, response);
            return;
        }
        if (username == null || email == null || firstName == null || lastName == null || role == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty() || role.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/addUser.jsp").forward(request, response);
            return;
        }


        try {
            // Check existence
            if (userService.usernameExists(username)) {
                request.setAttribute("errorMessage", "Username '" + username + "' already exists.");
                request.getRequestDispatcher("/addUser.jsp").forward(request, response);
                return;
            }
            if (userService.emailExists(email)) {
                request.setAttribute("errorMessage", "Email '" + email + "' is already registered.");
                request.getRequestDispatcher("/addUser.jsp").forward(request, response);
                return;
            }

            // Create and save user
            User user = new User();
            user.setUsername(username.trim());
            user.setPasswordHash(UserService.hashPassword(password)); // Hash the password
            user.setEmail(email.trim());
            user.setFirstName(firstName.trim());
            user.setLastName(lastName.trim());
            user.setRole(role);
            // Timestamps set automatically by User constructor

            userService.registerUser(user);
            session.setAttribute("adminUserSuccess", "User '" + username + "' successfully added.");
            response.sendRedirect("admin?action=manageUsers");

        } catch (RuntimeException e) { // Catch hashing or other runtime issues
            getServletContext().log("Error saving user (runtime)", e);
            request.setAttribute("errorMessage", "User registration failed: " + e.getMessage());
            request.getRequestDispatcher("/addUser.jsp").forward(request, response);
        } catch (IOException e) { // Catch file I/O issues
            getServletContext().log("Error saving user (I/O)", e);
            request.setAttribute("errorMessage", "User registration failed due to a data storage error.");
            request.getRequestDispatcher("/addUser.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String idParam = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String role = request.getParameter("role");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Basic validation
        if (username == null || email == null || firstName == null || lastName == null || role == null || idParam == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty() || role.trim().isEmpty() || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All fields (except password) are required.");
            // Need to re-fetch user data if ID is available
            forwardToEditUserWithError(request, response, idParam);
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e){
            request.setAttribute("errorMessage", "Invalid User ID format.");
            request.getRequestDispatcher("/manageUsers.jsp").forward(request, response); // Cant go back to edit form
            return;
        }


        try {
            User user = findUserById(id);
            if (user == null) {
                request.setAttribute("errorMessage", "User Not Found for update.");
                request.getRequestDispatcher("/manageUsers.jsp").forward(request, response); // Cant go back to edit form
                return;
            }

            // Keep original user data in request in case of validation errors
            request.setAttribute("userToEdit", user);

            // Validate and update password ONLY if a new one is provided
            boolean passwordProvided = (password != null && !password.isEmpty());
            if (passwordProvided) {
                if (!password.equals(confirmPassword)) {
                    request.setAttribute("errorMessage", "New passwords do not match.");
                    request.getRequestDispatcher("/editUser.jsp").forward(request, response);
                    return;
                }
                // Hash and set new password if validation passes
                user.setPasswordHash(UserService.hashPassword(password));
            }

            // Update other user details (check for uniqueness conflicts maybe? Optional)
            // Example: Check if new username/email conflicts with ANOTHER user
            // if (!user.getUsername().equalsIgnoreCase(username.trim()) && userService.usernameExists(username.trim())) { ... }
            // if (!user.getEmail().equalsIgnoreCase(email.trim()) && userService.emailExists(email.trim())) { ... }

            user.setUsername(username.trim());
            user.setEmail(email.trim());
            user.setFirstName(firstName.trim());
            user.setLastName(lastName.trim());
            user.setRole(role);
            user.setUpdatedAt(LocalDateTime.now()); // Update the 'updatedAt' timestamp

            userService.updateUser(user);
            session.setAttribute("adminUserSuccess", "User '" + user.getUsername() + "' successfully updated.");
            response.sendRedirect("admin?action=manageUsers");

        } catch (RuntimeException e) { // Catch hashing or other runtime issues
            getServletContext().log("Error updating user (runtime)", e);
            request.setAttribute("errorMessage", "Update failed: " + e.getMessage());
            request.getRequestDispatcher("/editUser.jsp").forward(request, response); // Forward back to edit form
        } catch (IOException e) { // Catch file I/O issues
            getServletContext().log("Error updating user (I/O)", e);
            request.setAttribute("errorMessage", "Update failed due to a data storage error.");
            request.getRequestDispatcher("/editUser.jsp").forward(request, response); // Forward back to edit form
        }
    }

    // Helper to forward back to edit user form with errors, reloading user data
    private void forwardToEditUserWithError(HttpServletRequest request, HttpServletResponse response, String idParam) throws ServletException, IOException {
        if (idParam != null) {
            try {
                User userToEdit = findUserById(Long.parseLong(idParam));
                request.setAttribute("userToEdit", userToEdit); // Set for the form even on error
            } catch (Exception ignored) { /* Ignore if user fetch fails here */ }
        }
        request.getRequestDispatcher("/editUser.jsp").forward(request, response);
    }


    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            Long deleteUserId = Long.parseLong(request.getParameter("id"));
            // Optional: Check if admin is trying to delete themselves? Prevent?
            // User loggedInAdmin = (User) session.getAttribute("user");
            // if (loggedInAdmin != null && loggedInAdmin.getId().equals(deleteUserId)) { ... }

            userService.deleteUser(deleteUserId);
            session.setAttribute("adminUserSuccess", "User successfully deleted.");
        } catch (NumberFormatException e) {
            session.setAttribute("adminUserError", "Invalid user ID for deletion.");
        } catch (IOException e) {
            getServletContext().log("Error deleting user", e);
            session.setAttribute("adminUserError", "Could not delete user due to data storage error.");
        }
        response.sendRedirect("admin?action=manageUsers"); // Redirect back to user list
    }


    // Helper method to find a user by ID using Streams
    private User findUserById(Long id) throws IOException {
        if (id == null) return null;
        return userService.getAllUsers().stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst()
                .orElse(null);
    }

    // =========================================================================
    // Helper Methods for Reservation Management
    // =========================================================================

    private void listAllReservations(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Reservation> allReservations = reservationService.getAllReservations();
        List<User> allUsers = userService.getAllUsers();
        List<Movie> allMovies = movieService.getAllMovies();

        // Create maps for efficient lookups
        Map<Long, String> userMap = allUsers.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername, (u1, u2) -> u1));
        Map<Long, String> movieMap = allMovies.stream()
                .collect(Collectors.toMap(Movie::getId, Movie::getTitle, (m1, m2) -> m1));

        List<AdminReservationView> reservationDetails = new ArrayList<>();
        for (Reservation res : allReservations) {
            String username = userMap.getOrDefault(res.getUserId(), "Unknown User (ID: " + res.getUserId() + ")");
            String movieTitle = movieMap.getOrDefault(res.getMovieId(), "Unknown Movie (ID: " + res.getMovieId() + ")");

            String formattedSeats = "N/A";
            if (res.getSelectedSeats() != null && !res.getSelectedSeats().isEmpty()) {
                formattedSeats = res.getSelectedSeats().stream().sorted().collect(Collectors.joining(", "));
            }

            Date utilDate = null; // java.util.Date
            if (res.getReservationTime() != null) {
                try {
                    utilDate = Date.from(res.getReservationTime().atZone(ZoneId.systemDefault()).toInstant());
                } catch (Exception e) {
                    getServletContext().log("Error converting reservation time for reservation ID " + res.getId(), e);
                }
            }

            reservationDetails.add(new AdminReservationView(
                    res.getId(),
                    username,
                    res.getUserId(),
                    movieTitle,
                    res.getShowtime(),
                    formattedSeats,
                    utilDate,
                    res.isReservationStatus()
            ));
        }

        // Sort by reservation time, oldest first (for queue order)
        reservationDetails.sort(Comparator.comparing(AdminReservationView::getReservationTime, Comparator.nullsLast(Comparator.naturalOrder())));

        // Load only non-approved reservations into the circular queue (oldest first)
        reservationQueue.clear();
        for (Reservation res : allReservations) {
            if (!res.isReservationStatus()) {
                reservationQueue.enqueue(res.getId().intValue());
            }
        }

        // Pass current (front) reservation details to JSP
        AdminReservationView currentQueueReservation = null;
        if (!reservationQueue.isEmpty()) {
            int currentId = reservationQueue.peek();
            for (AdminReservationView detail : reservationDetails) {
                if (detail.getReservationId().intValue() == currentId) {
                    currentQueueReservation = detail;
                    break;
                }
            }
        }

        request.setAttribute("allReservationDetails", reservationDetails);
        request.setAttribute("reservationQueue", reservationQueue);
        request.setAttribute("queueSize", reservationQueue.size());
        request.setAttribute("queueTickets", reservationQueue.getAllTickets());
        request.setAttribute("currentQueueReservation", currentQueueReservation);
        request.getRequestDispatcher("/manageReservations.jsp").forward(request, response);
    }

    /*
    // --- Optional: Admin Delete Reservation Handler Implementation ---
     private void adminDeleteReservation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String reservationIdParam = request.getParameter("reservationId");
        if (reservationIdParam == null || reservationIdParam.trim().isEmpty()) {
            session.setAttribute("adminReservationError", "Invalid request: Missing reservation ID.");
            // Redirect handled by caller doPost
            return;
        }
        try {
            long reservationId = Long.parseLong(reservationIdParam);
            // Admin doesn't need ownership check, just delete
            reservationService.deleteReservation(reservationId);
            session.setAttribute("adminReservationSuccess", "Reservation successfully deleted by admin.");
        } catch (NumberFormatException e) {
            session.setAttribute("adminReservationError", "Invalid reservation ID format.");
        } catch (IOException e) {
            getServletContext().log("Error admin deleting reservation ID " + reservationIdParam, e);
            session.setAttribute("adminReservationError", "Could not delete reservation due to a server error.");
        }
        // Redirect handled by caller doPost
     }
     */
}