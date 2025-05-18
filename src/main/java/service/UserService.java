//In service/UserService.java
package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.User;

public class UserService {
    private static final String DELIMITER = "||";
    private final Path usersFilePath;
    private final Object lock = new Object(); // Dedicated lock object

    public UserService(String filePath) {
        this.usersFilePath = Paths.get(filePath);
        initializeFile(); // Ensure file exists
    }

    private void initializeFile() {
        try {
            if (!Files.exists(usersFilePath.getParent())) {
                Files.createDirectories(usersFilePath.getParent());
            }
            if (!Files.exists(usersFilePath)) {
                Files.createFile(usersFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize user data file", e);
        }
    }

    public void registerUser(User user) throws IOException {
        synchronized (lock) { // Synchronize on the lock object
            List<User> users = getAllUsers();
            user.setId(getNextUserId(users));
            users.add(user);
            saveAllUsers(users);
        }
    }

    public boolean usernameExists(String username) throws IOException {
        return getAllUsers().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public boolean emailExists(String email) throws IOException {
        return getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
    public List<User> getAllUsers() throws IOException {
        if (!Files.exists(usersFilePath)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(usersFilePath, StandardCharsets.UTF_8)
                    .stream()
                    .map(this::parseUser)
                    .collect(Collectors.toList());
        } catch (IOException e){
            throw new IOException("Failed to get all users", e);
        }
    }

    private void saveAllUsers(List<User> users) throws IOException {
        List<String> lines = users.stream()
                .map(this::formatUser)
                .collect(Collectors.toList());

        try {
            Files.write(usersFilePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e){
            throw new IOException("Failed to save all users", e);
        }

    }

    private User parseUser(String line) {
        String[] parts = line.split("\\Q" + DELIMITER + "\\E");
        User user = new User();
        user.setId(Long.parseLong(parts[0]));
        user.setUsername(parts[1]);
        user.setPasswordHash(parts[2]);
        user.setEmail(parts[3]);
        user.setFirstName(parts[4]);
        user.setLastName(parts[5]);
        user.setCreatedAt(LocalDateTime.parse(parts[6]));
        user.setUpdatedAt(LocalDateTime.parse(parts[7]));
        user.setRole(parts[8]); // Parse the role
        return user;
    }

    private String formatUser(User user) {
        return String.join(DELIMITER,
                user.getId().toString(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString(),
                user.getRole()); // Format the role
    }

    private long getNextUserId(List<User> users) {
        return users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L) + 1;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e); // More specific exception
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public User authenticateUser(String username, String password) throws IOException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) &&
                    UserService.hashPassword(password).equals(user.getPasswordHash())) {
                return user; // Return the user object if credentials match
            }
        }
        return null; // Return null if no match is found
    }
    // Method to delete a user by ID
    public void deleteUser(Long userId) throws IOException {
        synchronized (lock) {
            List<User> users = getAllUsers();
            users.removeIf(user -> user.getId().equals(userId)); // Use removeIf for concise removal
            saveAllUsers(users);
        }
    }

    // Method to update an existing user
    public void updateUser(User updatedUser) throws IOException {
        synchronized (lock) {
            List<User> users = getAllUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(updatedUser.getId())) {
                    users.set(i, updatedUser); // Replace the old user with the updated user
                    break;
                }
            }
            saveAllUsers(users);
        }
    }
}