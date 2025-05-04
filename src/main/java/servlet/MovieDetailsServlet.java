// src/main/java/servlet/MovieDetailsServlet.java
package servlet;

import model.Movie;
import service.MovieService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/movie-details")
public class MovieDetailsServlet extends HttpServlet {

    private MovieService movieService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize MovieService (assuming the path is correct)
        String filePath = getServletContext().getRealPath("/WEB-INF/database/movies.txt");
        movieService = new MovieService(filePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String movieIdParam = request.getParameter("id");
        Movie movie = null;
        String errorMessage = null;

        if (movieIdParam != null && !movieIdParam.isEmpty()) {
            try {
                long movieId = Long.parseLong(movieIdParam);
                movie = movieService.getMovieById(movieId); // Fetch the movie by ID

                if (movie == null) {
                    errorMessage = "Movie not found.";
                }

            } catch (NumberFormatException e) {
                errorMessage = "Invalid movie ID format.";
                // Log the error if needed: getServletContext().log("Invalid movie ID format", e);
            } catch (IOException e) {
                errorMessage = "Error retrieving movie details: " + e.getMessage();
                // Log the error if needed: getServletContext().log("Error retrieving movie details", e);
            }
        } else {
            errorMessage = "Movie ID parameter is missing.";
        }

        if (movie != null) {
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/movieDetails.jsp").forward(request, response);
        } else {
            // Handle error - Option 1: Show an error message on the details page itself
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/movieDetails.jsp").forward(request, response);
            // Option 2: Redirect back to the movie list or home page with an error flag (less ideal for details)
            // response.sendRedirect("movies?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        }
    }
}