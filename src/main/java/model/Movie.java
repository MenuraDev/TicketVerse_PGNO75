package model;

import java.util.List;
import java.time.LocalDate;

public class Movie {
    private Long id;
    private String title;
    private String poster; //  URL or file path to the poster image
    private String synopsis;
    private String genre;
    private int duration; // Duration in minutes
    private String rating; // e.g., "PG", "R", "PG-13"
    private String director;
    private String cast;
    private String trailerURL; // Optional
    private List<String> showtimes; // List of show date/time strings
    private String movieStatus; // New field: "Coming Soon" or "Now Showing"
    private LocalDate releaseDate; // New field for movie release date

    // Constructors
    public Movie() {}

    public Movie(String title, String poster, String synopsis, String genre, int duration,
                 String rating, String director, String cast, String trailerURL, List<String> showtimes,
                 String movieStatus, LocalDate releaseDate) {
        this.title = title;
        this.poster = poster;
        this.synopsis = synopsis;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.director = director;
        this.cast = cast;
        this.trailerURL = trailerURL;
        this.showtimes = showtimes;
        this.movieStatus = movieStatus;
        this.releaseDate = releaseDate;
    }

    // Getters and setters (all fields)
    public String getMovieStatus() {
        return movieStatus;
    }

    public void setMovieStatus(String movieStatus) {
        this.movieStatus = movieStatus;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }
    public String getTrailerURL() {
        return trailerURL;
    }

    public void setTrailerURL(String trailerURL) {
        this.trailerURL = trailerURL;
    }

    public List<String> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<String> showtimes) {
        this.showtimes = showtimes;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}