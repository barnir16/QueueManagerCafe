package app.server.service;

import app.server.dao.UserDaoImpl;
import app.shared.User;

public class UserService {
    private final UserDaoImpl userDao;

    // Existing default constructor
    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    // NEW constructor for testing
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public boolean authenticate(String username, String password) {
        User user = userDao.find(username);
        return user != null && user.getPassword().equals(password);
    }

    public boolean register(String username, String password) {
        return userDao.save(new User(username, password));
    }
}
