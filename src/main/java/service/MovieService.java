package service;

import model.Movie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// MovieService class to manage movie operations

public class MovieService {

    private static final String DELIMITER = "||"; //delimiter for the movie data
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; 
    private final Path moviesFilePath; 
    private final Object lock = new Object(); 

    public MovieService(String filePath) {
        this.moviesFilePath = Paths.get(filePath);
        initializeFile();
    }

    // Initialize the movie data file
    private void initializeFile() {
        try {
            if (!Files.exists(moviesFilePath.getParent())) { //check if the parent directory exists
                Files.createDirectories(moviesFilePath.getParent()); //create the parent directory
            }
            if (!Files.exists(moviesFilePath)) { //check if the file exists
                Files.createFile(moviesFilePath); //create the file
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize movie data file", e);
        }
    }

    // Add a new movie to the data file
    public void addMovie(Movie movie) throws IOException {
        synchronized (lock){ //synchronize the addMovie method 
            List<Movie> movies = getAllMovies(); //get all the movies from the data file as a list
            movie.setId(getNextMovieId(movies)); //set the id of the new movie
            movies.add(movie); //add the new movie to the list
            saveAllMovies(movies); //save to movie.txt
        }
    }

    // Get all movies from the data file
    public List<Movie> getAllMovies() throws IOException {
        if (!Files.exists(moviesFilePath)) { //check if the file exists
            return new ArrayList<>(); //return an empty list if the file does not exist
        }
        try {
            return Files.readAllLines(moviesFilePath, StandardCharsets.UTF_8) 
                    .stream() //stream the lines of the file
                    .map(this::parseMovie) //parse the lines from movie.txt
                    .collect(Collectors.toList()); //convert the lines to  list
        } catch (IOException e){
            throw new IOException("Failed to get all movies", e);
        }
    }

    // Save all movies to the data file
    private void saveAllMovies(List<Movie> movies) throws IOException {
        List<String> lines = movies.stream()
                .map(this::formatMovie) //format the movies to movie.txt
                .collect(Collectors.toList()); //convert the movies to a list (string)

        try {
            Files.write(moviesFilePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); //write to movie.txt
        } catch (IOException e) {
            throw new IOException("Failed to save all users", e);
        }
    }

    // set the movies from movie.txt
    private Movie parseMovie(String line) {
        String[] parts = line.split("\\Q" + DELIMITER + "\\E"); //split the line by the delimiter
        Movie movie = new Movie(); //create a new movie object
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
        movie.setShowtimes(Arrays.asList(parts[10].split(",")));
        movie.setMovieStatus(parts[11]);
        // set release date if it exists
        if (parts.length > 12 && !parts[12].isEmpty()) {
            movie.setReleaseDate(LocalDate.parse(parts[12], DATE_FORMATTER)); //set the release date
        }
        return movie;
    }

    //get the movies from movie.txt
    private String formatMovie(Movie movie) {
        String showtimesString = String.join(",", movie.getShowtimes()); //join the showtimes
        String releaseDateString = movie.getReleaseDate() != null ? 
            movie.getReleaseDate().format(DATE_FORMATTER) : ""; //get the release date as date 

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
                showtimesString,
                movie.getMovieStatus(),
                releaseDateString
        );
    }

    //get new movie id
    private long getNextMovieId(List<Movie> movies) {
        return movies.stream()
                .mapToLong(Movie::getId) //map the movies to their ids
                .max() //get the max movie id
                .orElse(0L) + 1; //else no movie id, return 0 and add 1
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

    //get movie by id
    public Movie getMovieById(Long id) throws IOException {
        List<Movie> movies = getAllMovies(); //get all the movies from movie.txt as list
        for (Movie movie : movies){
            if(movie.getId().equals(id)){
                return movie;
            }
        }
        return null;
    }
}