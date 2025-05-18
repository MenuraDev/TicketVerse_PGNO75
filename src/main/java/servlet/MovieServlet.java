package servlet;
import model.Movie;
import model.User;
import service.MovieService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
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
        if (session == null || session.getAttribute("user") == null || !"admin".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect("login.jsp"); // Redirect if not admin
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "manageMovies"; // Default: Show manageMovies
        }

        try {
            switch (action) {
                case "addMovie":
                    request.getRequestDispatcher("/addMovie.jsp").forward(request, response);
                    break;
                case "editMovie":
                    Long movieId = Long.parseLong(request.getParameter("id"));
                    Movie movie = movieService.getMovieById(movieId);  // Get movie by ID
                    if (movie != null) {
                        request.setAttribute("movie", movie);
                        request.getRequestDispatcher("/editMovie.jsp").forward(request, response);
                    } else {
                        // Handle movie not found (e.g., redirect to manageMovies with an error message)
                        response.sendRedirect("movie?action=manageMovies&error=MovieNotFound");
                    }
                    break;
                case "deleteMovie":
                    Long deleteId = Long.parseLong(request.getParameter("id"));
                    movieService.deleteMovie(deleteId);
                    response.sendRedirect("movie?action=manageMovies"); // Redirect back to movie list
                    break;
                case "manageMovies":
                    List<Movie> movies = movieService.getAllMovies();
                    request.setAttribute("movies", movies);
                    request.getRequestDispatcher("/manageMovies.jsp").forward(request, response);
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
        if (session == null || session.getAttribute("user") == null || !"admin".equals(((User) session.getAttribute("user")).getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        try{
            switch (action) {
                case "saveMovie":
                    saveMovie(request, response);
                    break;
                case "updateMovie":
                    updateMovie(request, response);  // Handle movie updates
                    break;
                default:
                    response.sendRedirect("admin");
            }
        }catch (IOException e){
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request,response); // Go back to the form
        }
    }


    //add movies
    private void saveMovie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //get the movie details
        String title = request.getParameter("title");
        String poster = request.getParameter("poster");
        String synopsis = request.getParameter("synopsis");
        String genre = request.getParameter("genre");
        int duration = Integer.parseInt(request.getParameter("duration"));
        String rating = request.getParameter("rating");
        String director = request.getParameter("director");
        String cast = request.getParameter("cast");
        String trailerURL = request.getParameter("trailerURL");
        String movieStatus = request.getParameter("movieStatus");
        LocalDate releaseDate = LocalDate.parse(request.getParameter("releaseDate"));

        String[] showtimesArray = request.getParameterValues("showtimes");
        List<String> showtimes = new ArrayList<>();
        if (showtimesArray != null) {
            showtimes = Arrays.asList(showtimesArray);
        }

        //create a new movie object
        Movie movie = new Movie(title, poster, synopsis, genre, duration, rating, director, cast, trailerURL, showtimes, movieStatus, releaseDate);

        try {
            movieService.addMovie(movie); //save to movie.txt
            response.sendRedirect("movie?action=manageMovies"); //redirect to manageMovies
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Failed to save movie: " + e.getMessage());
            request.getRequestDispatcher("/addMovie.jsp").forward(request,response);
        }
    }

    private void updateMovie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Long id = Long.parseLong(request.getParameter("id"));
        String title = request.getParameter("title");
        String poster = request.getParameter("poster");
        String synopsis = request.getParameter("synopsis");
        String genre = request.getParameter("genre");
        int duration = Integer.parseInt(request.getParameter("duration"));
        String rating = request.getParameter("rating");
        String director = request.getParameter("director");
        String cast = request.getParameter("cast");
        String trailerURL = request.getParameter("trailerURL");
        String movieStatus = request.getParameter("movieStatus");
        LocalDate releaseDate = LocalDate.parse(request.getParameter("releaseDate"));

        String[] showtimesArray = request.getParameterValues("showtimes");
        List<String> showtimes = new ArrayList<>();
        if (showtimesArray != null) {
            showtimes = Arrays.asList(showtimesArray);
        }

        Movie updatedMovie = new Movie(title, poster, synopsis, genre, duration, rating, director, cast, trailerURL, showtimes, movieStatus, releaseDate);
        updatedMovie.setId(id);

        try {
            movieService.updateMovie(updatedMovie);
            response.sendRedirect("movie?action=manageMovies");
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Failed to save movie: " + e.getMessage());
            request.getRequestDispatcher("/editMovie.jsp").forward(request,response);
        }
    }
}