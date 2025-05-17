package servlet;

import model.Movie;
//import model.User;
import service.MovieService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/movie")
public class MovieServlet extends HttpServlet {

    private MovieService movieService;

    @Override
    public void init() throws ServletException {
        super.init();
        String filePath = getServletContext().getRealPath("/WEB-INF/database/movies.txt");
        movieService = new MovieService(filePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        /*
        if (session == null || session.getAttribute("user") == null || !"admin".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect("login.jsp"); // Redirect if not admin
            return;
        }
*/
        String action = request.getParameter("action");
        if (action == null) {
            action = "manageMovies"; // Default: Show manageMovies
        }

        try {
            switch (action) {
                case "addMovie":
                    request.getRequestDispatcher("/addMovie.jsp").forward(request, response);
                    break;

                default:
                    response.sendRedirect("admin");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request,response); // Go back to the form

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        /*
        if (session == null || session.getAttribute("user") == null || !"admin".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }
*/
        String action = request.getParameter("action");
        try{
            switch (action) {
                case "saveMovie":
                    saveMovie(request, response);
                    break;

                default:
                    response.sendRedirect("admin");
            }
        }catch (IOException e){
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request,response); // Go back to the form
        }
    }



    private void saveMovie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String title = request.getParameter("title");
        String poster = request.getParameter("poster");
        String synopsis = request.getParameter("synopsis");
        String genre = request.getParameter("genre");
        int duration = Integer.parseInt(request.getParameter("duration")); // Handle potential NumberFormatException
        String rating = request.getParameter("rating");
        String director = request.getParameter("director");
        String cast = request.getParameter("cast");
        String trailerURL = request.getParameter("trailerURL");
        String movieStatus = request.getParameter("movieStatus"); // Get movie status

        // Handle multiple showtimes (important part)
        String[] showtimesArray = request.getParameterValues("showtimes");
        List<String> showtimes = new ArrayList<>();
        if (showtimesArray != null) {
            showtimes = Arrays.asList(showtimesArray);  // Convert to List
        }


        Movie movie = new Movie(title, poster, synopsis, genre, duration, rating, director, cast, trailerURL, showtimes,movieStatus);

        try {
            movieService.addMovie(movie);
            response.sendRedirect("movie?action=manageMovies"); // Redirect to manage movies
        } catch (IOException e) {
            // Handle file writing errors
            request.setAttribute("errorMessage", "Failed to save movie: " + e.getMessage());
            request.getRequestDispatcher("/addMovie.jsp").forward(request,response); // Go back to the form
        }
    }


}