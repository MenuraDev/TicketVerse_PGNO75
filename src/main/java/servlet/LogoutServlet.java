package servlet;
import utils.CookieUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Don't create a new session if one doesn't exist
        if (session != null) {
            session.invalidate(); // Invalidate the session
        }

        // Delete the login cookie
        CookieUtils.deleteLoginCookie(response);

        response.sendRedirect("home"); // Redirect to the home page (or login page)
    }
}