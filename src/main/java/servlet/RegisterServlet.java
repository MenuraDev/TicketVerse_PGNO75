package servlet;

import model.User;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        String filePath = getServletContext().getRealPath("/WEB-INF/database/users.txt"); // Corrected path
        userService = new UserService(filePath);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            request.getRequestDispatcher("/register.jsp").forward(request, response); // Corrected path
            return;
        }

        try {
            if (userService.usernameExists(username)) {
                request.setAttribute("errorMessage", "Username already exists");
                request.getRequestDispatcher("/register.jsp").forward(request, response); // Corrected path
                return;
            }

            if (userService.emailExists(email)) {
                request.setAttribute("errorMessage", "Email already registered");
                request.getRequestDispatcher("/register.jsp").forward(request, response); // Corrected path
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(UserService.hashPassword(password)); // Hash the password
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setRole("user");

            userService.registerUser(user);

            HttpSession session = request.getSession();
            session.setAttribute("registerSuccess", true);
            response.sendRedirect("login.jsp"); // Redirect to login page after successful registration

        } catch (RuntimeException e) {
            // Catch hashing exceptions
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (IOException e) {
            //Catch file exceptions
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);

        }
    }
}