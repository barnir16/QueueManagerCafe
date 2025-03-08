package app.server.service;

import app.server.dao.IDao;
import app.server.dao.MyDMFileImpl;
import app.shared.User;

import java.util.logging.Logger;

/**
 * MainCafeService - main business logic class for storing/finding Users.
 */
public class MainCafeService {
    private static final Logger logger = Logger.getLogger(MainCafeService.class.getName());
    private final IDao<User> dao;

    // Default constructor uses a file-based DAO with "users.txt"
    public MainCafeService() {
        this.dao = new MyDMFileImpl<>("users.txt");
    }

    // For testing or custom injection
    public MainCafeService(IDao<User> dao) {
        this.dao = dao;
    }

    /**
     * Saves a new User to the file.
     * @param username e.g. "User1"
     * @param password e.g. "password123"
     * @return true if saved, false if user already exists
     */
    public boolean saveUser(String username, String password) {
        // Check if it already exists
        User existing = dao.find(username);
        if (existing != null) {
            logger.warning("User with username '" + username + "' already exists.");
            return false;
        }
        User newUser = new User(username, password);
        return dao.save(newUser);
    }

    /**
     * Finds a User by username (ID).
     */
    public User findUser(String username) {
        return dao.find(username);
    }
}
