// src/main/java/service/ReservationService.java
package service;

import model.Reservation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.DateTimeException;

public class ReservationService {

  private static final String DELIMITER = "||";
  private static final String SEAT_DELIMITER = ","; // Delimiter for seats within the string
  private final Path reservationsFilePath;
  private final Object lock = new Object(); // Dedicated lock for file access

  public ReservationService(String filePath) {
    this.reservationsFilePath = Paths.get(filePath);
    initializeFile();
  }

  private void initializeFile() {
    try {
      if (!Files.exists(reservationsFilePath.getParent())) {
        Files.createDirectories(reservationsFilePath.getParent());
      }
      if (!Files.exists(reservationsFilePath)) {
        Files.createFile(reservationsFilePath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize reservations data file", e);
    }
  }

  public void addReservation(Reservation reservation) throws IOException {
    synchronized (lock) {
      List<Reservation> reservations = getAllReservations();
      // Optional: Add another check here to prevent double booking if needed (though BookingServlet should handle it)
      reservation.setId(getNextReservationId(reservations));
      reservations.add(reservation);
      saveAllReservations(reservations);
    }
  }

  public List<Reservation> getAllReservations() throws IOException {
    // No lock needed for read usually, but since parsing can fail,
    // and to be consistent with the write lock, we lock read too.
    // For better performance with many reads, consider ReadWriteLock.
    synchronized (lock) {
      if (!Files.exists(reservationsFilePath)) {
        return new ArrayList<>();
      }
      try {
        return Files.readAllLines(reservationsFilePath, StandardCharsets.UTF_8)
            .stream()
            .filter(line -> !line.trim().isEmpty()) // Ignore empty lines
            .map(this::parseReservation)
            .collect(Collectors.toList());
      } catch (IOException e) {
        System.err.println("Error reading reservations file: " + e.getMessage());
        throw new IOException("Failed to get all reservations", e);
      } catch (Exception e) { // Catch parsing errors
        System.err.println("Error parsing reservations file: " + e.getMessage());
        // Depending on requirements, you might return an empty list,
        // throw a custom exception, or try to recover partially.
        throw new RuntimeException("Failed to parse reservation data", e);
      }
    }
  }

  public void saveAllReservations(List<Reservation> reservations) throws IOException {
    List<String> lines = reservations.stream()
        .map(this::formatReservation)
        .collect(Collectors.toList());

    // Synchronize write operation
    synchronized (lock) {
      try {
        Files.write(reservationsFilePath, lines, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      } catch (IOException e) {
        System.err.println("Error writing reservations file: " + e.getMessage());
        throw new IOException("Failed to save all reservations", e);
      }
    }
  }

  private Reservation parseReservation(String line) {
    try {
      String[] parts = line.split("\\Q" + DELIMITER + "\\E");
      if (parts.length < 7) { // Basic check for enough parts
        throw new IllegalArgumentException("Invalid reservation data format: " + line);
      }

      Reservation reservation = new Reservation();
      reservation.setId(Long.parseLong(parts[0]));
      reservation.setMovieId(Long.parseLong(parts[1]));
      reservation.setShowtime(parts[2]);
      reservation.setUserId(Long.parseLong(parts[3]));

      // Handle potentially empty seat string
      List<String> seats = new ArrayList<>();
      if (parts[4] != null && !parts[4].trim().isEmpty()) {
        seats = Arrays.asList(parts[4].split("\\Q" + SEAT_DELIMITER + "\\E"));
      }
      reservation.setSelectedSeats(seats);

      reservation.setBeverages(parts[5]); // Can be empty
      reservation.setReservationTime(LocalDateTime.parse(parts[6]));
      // Read reservationStatus if present, else default to false
      if (parts.length >= 8) {
        reservation.setReservationStatus(Boolean.parseBoolean(parts[7]));
      } else {
        reservation.setReservationStatus(false);
      }
      return reservation;

    } catch (NumberFormatException | DateTimeException | ArrayIndexOutOfBoundsException e) {
      // Log the error and the problematic line
      System.err.println("Error parsing reservation line: '" + line + "'. Error: " + e.getMessage());
      // Re-throw as a RuntimeException or a custom checked exception
      throw new RuntimeException("Failed to parse reservation data: " + line, e);
    }
  }




}