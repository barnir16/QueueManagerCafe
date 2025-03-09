package app.server.service;

import app.server.dao.IDao;
import app.server.dao.MyDMFileImpl;
import app.shared.User;

/**
 * A simple service for user registration & authentication.
 */
public class UserService {
    private final IDao<User> userDao;

    // Default constructor -> uses "users.txt"
    public UserService() {
        this.userDao = new MyDMFileImpl("users.txt");
    }

    // For testing or injection
    public UserService(IDao<User> userDao) {
        this.userDao = userDao;
    }

    public boolean authenticate(String username, String password) {
        User user = userDao.find(username);
        return (user != null && user.getPassword().equals(password));
    }

    public boolean register(String username, String password) {
        // Return false if user already exists
        return userDao.save(new User(username, password));
    }
}
