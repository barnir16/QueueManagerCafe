package app.Server.dao;

import app.shared.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class.getName());
    private static final String FILE_PATH = "users.txt";
    private final Map<String, User> users = new HashMap<>();

    public UserDaoImpl() {
        loadUsers();
    }

    @Override
    public synchronized User find(String username) {
        return users.get(username);
    }

    @Override
    public synchronized boolean save(User user) {
        if (users.containsKey(user.getUsername())) {
            logger.warning("Attempted to save duplicate user: " + user.getUsername());
            return false;
        }

        users.put(user.getUsername(), user);
        writeUsersToFile();
        return true;
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();  // Ensure file exists
                logger.info("User file not found, created new: " + FILE_PATH);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create user file", e);
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    logger.warning("Skipping malformed entry: " + line);
                    continue;
                }
                users.put(parts[0], new User(parts[0], parts[1]));
            }
            logger.info("User data loaded successfully. Total users: " + users.size());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load users from file", e);
        }
    }

    private synchronized void writeUsersToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users.values()) {
                bw.write(user.getUsername() + "," + user.getPassword());
                bw.newLine();
            }
            logger.info("User data written to file successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing users to file", e);
        }
    }
}
