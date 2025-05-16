//In service/UserService.java
package service;

import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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