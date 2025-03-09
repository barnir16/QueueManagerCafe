package app.Server.dao;

import app.shared.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Single file-based DAO for User objects. Implements IDao<User>.
 */
public class MyDMFileImpl implements IDao<User> {
    private static final Logger logger = Logger.getLogger(MyDMFileImpl.class.getName());

    private final String filePath;
    private final Map<String, User> users = new HashMap<>();

    /**
     * No-arg constructor defaults to "users.txt"
     */
    public MyDMFileImpl() {
        this.filePath = "users.txt";
        // Optionally deleteFileIfExists() if you want a fresh file every time
        // deleteFileIfExists();
        loadUsers();
    }

    /**
     * Allows a custom file path (e.g. "test_users.txt").
     */
    public MyDMFileImpl(String filePath) {
        this.filePath = filePath;
        // Optionally deleteFileIfExists() if you want a fresh file every time
        // deleteFileIfExists();
        loadUsers();
    }

    @Override
    public synchronized User find(String username) {
        return users.get(username);
    }

    @Override
    public synchronized boolean save(User user) {
        // If user already exists, fail
        if (users.containsKey(user.getUsername())) {
            logger.warning("Attempted to save duplicate user: " + user.getUsername());
            return false;
        }
        // Otherwise, store & persist
        users.put(user.getUsername(), user);
        writeUsersToFile();
        return true;
    }

    /**
     * Loads users from the file into memory.
     */
    private void loadUsers() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                logger.info("User file not found, created new: " + filePath);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create user file", e);
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            users.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], new User(parts[0], parts[1]));
                } else {
                    logger.warning("Skipping malformed line: " + line);
                }
            }
            logger.info("User data loaded successfully. Total users: " + users.size());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load users from file", e);
        }
    }

    /**
     * Writes the in-memory users map back to file.
     */
    private synchronized void writeUsersToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users.values()) {
                bw.write(user.getUsername() + "," + user.getPassword());
                bw.newLine();
            }
            logger.info("User data written to file successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing users to file", e);
        }
    }

    /**
     * Utility if you want a fresh environment on every new MyDMFileImpl instance.
     */
    @SuppressWarnings("unused")
    private void deleteFileIfExists() {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                logger.info("Deleted existing file: " + filePath);
            } else {
                logger.warning("Failed to delete file: " + filePath);
            }
        }
    }
}
