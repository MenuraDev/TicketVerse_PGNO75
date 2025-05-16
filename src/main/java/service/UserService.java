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