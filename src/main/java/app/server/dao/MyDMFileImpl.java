package app.server.dao;

import app.shared.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDMFileImpl implements IDao<User> {
    private static final Logger logger = Logger.getLogger(MyDMFileImpl.class.getName());

    private final String filePath;
    private final Map<String, User> users = new HashMap<>();

    // 1) No-arg constructor uses "users.txt"
    public MyDMFileImpl() {
        this.filePath = "users.txt";
        loadUsers();
    }

    // 2) String-arg constructor allows a custom file path
    public MyDMFileImpl(String filePath) {
        this.filePath = filePath;
        loadUsers();
    }

    @Override
    public User find(String username) {
        return users.get(username);
    }

    @Override
    public boolean save(User user) {
        if (users.containsKey(user.getUsername())) {
            logger.warning("User already exists: " + user.getUsername());
            return false;
        }
        users.put(user.getUsername(), user);
        writeUsersToFile();
        return true;
    }

    private void loadUsers() {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("No existing file found at " + filePath + ". Starting fresh.");
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
            logger.info("Users loaded from " + filePath + ": " + users.size() + " total.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load users from file", e);
        }
    }

    private void writeUsersToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users.values()) {
                bw.write(user.getUsername() + "," + user.getPassword());
                bw.newLine();
            }
            logger.info("Users saved to " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write users to file", e);
        }
    }
}
