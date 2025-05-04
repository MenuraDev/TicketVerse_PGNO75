package service;

import model.Movie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {

    private static final String DELIMITER = "||";
    private final Path moviesFilePath;
    private final Object lock = new Object();

    public MovieService(String filePath) {
        this.moviesFilePath = Paths.get(filePath);
        initializeFile();
    }

    private void initializeFile() {
        try {
            if (!Files.exists(moviesFilePath.getParent())) {
                Files.createDirectories(moviesFilePath.getParent());
            }
            if (!Files.exists(moviesFilePath)) {
                Files.createFile(moviesFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize movie data file", e);
        }
    }

    public void addMovie(Movie movie) throws IOException {
        synchronized (lock){
            List<Movie> movies = getAllMovies();
            movie.setId(getNextMovieId(movies));
            movies.add(movie);
            saveAllMovies(movies);
        }
    }

    public List<Movie> getAllMovies() throws IOException {
        if (!Files.exists(moviesFilePath)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(moviesFilePath, StandardCharsets.UTF_8)
                    .stream()
                    .map(this::parseMovie)
                    .collect(Collectors.toList());
        } catch (IOException e){
            throw new IOException("Failed to get all movies", e);
        }
    }


    private void saveAllMovies(List<Movie> movies) throws IOException {
        List<String> lines = movies.stream()
                .map(this::formatMovie)
                .collect(Collectors.toList());

        try {
            Files.write(moviesFilePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to save all users", e);
        }
    }


    private Movie parseMovie(String line) {
        String[] parts = line.split("\\Q" + DELIMITER + "\\E");
        Movie movie = new Movie();
        movie.setId(Long.parseLong(parts[0]));
        movie.setTitle(parts[1]);
        movie.setPoster(parts[2]);
        movie.setSynopsis(parts[3]);
        movie.setGenre(parts[4]);
        movie.setDuration(Integer.parseInt(parts[5]));
        movie.setRating(parts[6]);
        movie.setDirector(parts[7]);
        movie.setCast(parts[8]);
        movie.setTrailerURL(parts[9]);
        // Showtimes are stored as a comma-separated string within the larger delimited string.
        movie.setShowtimes(Arrays.asList(parts[10].split(",")));
        movie.setMovieStatus(parts[11]); // Parse movieStatus
        return movie;
    }

    private String formatMovie(Movie movie) {
        // Join the showtimes list into a single comma-separated string.
        String showtimesString = String.join(",", movie.getShowtimes());

        return String.join(DELIMITER,
                movie.getId().toString(),
                movie.getTitle(),
                movie.getPoster(),
                movie.getSynopsis(),
                movie.getGenre(),
                String.valueOf(movie.getDuration()),
                movie.getRating(),
                movie.getDirector(),
                movie.getCast(),
                movie.getTrailerURL(),
                showtimesString, // Use the comma-separated showtimes string
                movie.getMovieStatus() // Format movieStatus
        );
    }


    private long getNextMovieId(List<Movie> movies) {
        return movies.stream()
                .mapToLong(Movie::getId)
                .max()
                .orElse(0L) + 1;
    }
    public void updateMovie(Movie updatedMovie) throws IOException {
        synchronized (lock) {
            List<Movie> movies = getAllMovies();
            for (int i = 0; i < movies.size(); i++) {
                if (movies.get(i).getId().equals(updatedMovie.getId())) {
                    movies.set(i, updatedMovie);
                    break;
                }
            }
            saveAllMovies(movies);
        }
    }

    public void deleteMovie(Long movieId) throws IOException {
        synchronized (lock){
            List<Movie> movies = getAllMovies();
            movies.removeIf(movie -> movie.getId().equals(movieId));
            saveAllMovies(movies);
        }
    }
    public Movie getMovieById(Long id) throws IOException {
        List<Movie> movies = getAllMovies();
        for (Movie movie : movies){
            if(movie.getId().equals(id)){
                return movie;
            }
        }
        return null;
    }
}