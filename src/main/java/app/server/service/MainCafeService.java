package app.server.service;

import app.server.dao.IDao;
import app.server.dao.MyDMFileImpl;
import app.shared.User;

import java.util.logging.Logger;

public class MainCafeService {
    private static final Logger logger = Logger.getLogger(MainCafeService.class.getName());
    private final IDao<User> dao;

    // Default constructor uses "users.txt"
    public MainCafeService() {
        this.dao = new MyDMFileImpl("users.txt"); // ✅ No <>
    }

    public MainCafeService(IDao<User> dao) {
        this.dao = dao;
    }

    public boolean saveUser(String username, String password) {
        User existing = dao.find(username);
        if (existing != null) {
            logger.warning("User with username '" + username + "' already exists.");
            return false;
        }
        User newUser = new User(username, password);
        return dao.save(newUser);
    }

    public User findUser(String username) {
        return dao.find(username);
    }
}
