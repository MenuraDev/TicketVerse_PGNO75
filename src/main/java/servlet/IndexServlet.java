// src/main/java/servlet/IndexServlet.java
package servlet;

import model.Movie;
import service.MovieService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Map this servlet to handle requests for the home page
// You might use "/", "/home", or map it specifically to index.jsp in web.xml
@WebServlet(name = "IndexServlet", urlPatterns = {"/home", ""}) // Handles root context and /home
public class IndexServlet extends HttpServlet {

    private MovieService movieService;
    private static final int MAX_MOVIES_PER_SECTION = 3; // Define limit

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize MovieService
        String filePath = getServletContext().getRealPath("/WEB-INF/database/movies.txt");
        movieService = new MovieService(filePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Get all movies
            List<Movie> allMovies = movieService.getAllMovies();

            // 2. Filter into Now Showing and Coming Soon lists
            List<Movie> nowShowing = allMovies.stream()
                    .filter(movie -> "Now Showing".equalsIgnoreCase(movie.getMovieStatus()))
                    // Sort by ID descending (proxy for latest added)
                    .sorted(Comparator.comparing(Movie::getId).reversed())
                    // Limit to the top N
                    .limit(MAX_MOVIES_PER_SECTION)
                    .collect(Collectors.toList());

            List<Movie> comingSoon = allMovies.stream()
                    .filter(movie -> "Coming Soon".equalsIgnoreCase(movie.getMovieStatus()))
                    // Sort by ID descending
                    .sorted(Comparator.comparing(Movie::getId).reversed())
                    // Limit to the top N
                    .limit(MAX_MOVIES_PER_SECTION)
                    .collect(Collectors.toList());

            // 3. Set attributes for the JSP
            request.setAttribute("latestNowShowing", nowShowing);
            request.setAttribute("latestComingSoon", comingSoon);

        } catch (IOException e) {
            // Log error and potentially set an error message for the user
            getServletContext().log("Error fetching movies for index page", e);
            request.setAttribute("indexPageError", "Could not load movie listings. Please try again later.");
        }

        // 4. Forward to the index.jsp view
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    // doPost might not be needed for index page, but include if necessary
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp); // Or handle specific POST actions if any
    }
}