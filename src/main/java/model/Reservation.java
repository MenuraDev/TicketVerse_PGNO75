// src/main/java/model/Reservation.java
package model;

import java.time.LocalDateTime;
import java.util.List;

public class Reservation {
  private Long id;
  private Long movieId;
  private String showtime; // Store the specific showtime string (e.g., "2024-08-15T19:00")
  private Long userId;
  private List<String> selectedSeats; // e.g., ["A1", "A2", "C5"]
  private LocalDateTime reservationTime;
  private boolean reservationStatus; // false = pending, true = approved

  // Constructors
  public Reservation() {
    this.reservationTime = LocalDateTime.now(); // Set timestamp on creation
    this.reservationStatus = false;
  }

  public Reservation(Long movieId, String showtime, Long userId, List<String> selectedSeats, String beverages) {
    this(); // Call default constructor to set timestamp
    this.movieId = movieId;
    this.showtime = showtime;
    this.userId = userId;
    this.selectedSeats = selectedSeats;
    this.reservationStatus = false;
  }

  // --- Getters and Setters ---
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMovieId() {
    return movieId;
  }

  public void setMovieId(Long movieId) {
    this.movieId = movieId;
  }

  public String getShowtime() {
    return showtime;
  }

  public void setShowtime(String showtime) {
    this.showtime = showtime;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<String> getSelectedSeats() {
    return selectedSeats;
  }

  public void setSelectedSeats(List<String> selectedSeats) {
    this.selectedSeats = selectedSeats;
  }

  public LocalDateTime getReservationTime() {
    return reservationTime;
  }

  public void setReservationTime(LocalDateTime reservationTime) {
    this.reservationTime = reservationTime;
  }

  public boolean isReservationStatus() {
    return reservationStatus;
  }

  public void setReservationStatus(boolean reservationStatus) {
    this.reservationStatus = reservationStatus;
  }
}