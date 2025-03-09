package app.Server.service;

import app.Server.dao.IDao;
import app.Server.dao.MyDMFileImpl;
import app.shared.User;

/**
 * A simple service for user registration & authentication.
 */
public class UserService {
    private final IDao<User> userDao;

    // Default constructor: uses "users.txt"
    public UserService() {
        this.userDao = new MyDMFileImpl("users.txt");
    }

    // Constructor for testing or custom injection
    public UserService(IDao<User> userDao) {
        this.userDao = userDao;
    }

    /**
     * Authenticates a user by username & password.
     */
    public boolean authenticate(String username, String password) {
        User user = userDao.find(username);
        return user != null && user.getPassword().equals(password);
    }

    /**
     * Registers a new user if they don't already exist.
     */
    public boolean register(String username, String password) {
        return userDao.save(new User(username, password));
    }
}
