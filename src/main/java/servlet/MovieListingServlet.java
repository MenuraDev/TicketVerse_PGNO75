package servlet;

import model.Movie;
import service.MovieService;
import utils.MovieSorter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/movies")
public class MovieListingServlet extends HttpServlet {
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
        String status = request.getParameter("status");
        List<Movie> movies = movieService.getAllMovies();
        
        // Sort movies by release date using insertion sort
        MovieSorter.sortByReleaseDate(movies);
        
        if ("nowshowing".equalsIgnoreCase(status)) {
            movies = movies.stream()
                    .filter(movie -> "Now Showing".equalsIgnoreCase(movie.getMovieStatus()))
                    .collect(Collectors.toList());
            request.setAttribute("movies", movies);
            request.getRequestDispatcher("/nowShowing.jsp").forward(request, response);
        } else if ("comingsoon".equalsIgnoreCase(status)) {
            movies = movies.stream()
                    .filter(movie -> "Coming Soon".equalsIgnoreCase(movie.getMovieStatus()))
                    .collect(Collectors.toList());
            request.setAttribute("movies", movies);
            request.getRequestDispatcher("/comingSoon.jsp").forward(request, response);
        } else {
            request.setAttribute("movies", movies);
            request.getRequestDispatcher("/allMovies.jsp").forward(request, response);
        }
    }
}