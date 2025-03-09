package app.server.service;

import app.server.dao.IDao;
import app.server.dao.MyDMFileImpl;
import app.shared.User;
import java.util.logging.Logger;

/**
 * Another example service class for "saveUser"/"findUser" calls,
 * used by HandleRequest's "saveUser"/"findUser" actions.
 */
public class MainCafeService {
    private static final Logger logger = Logger.getLogger(MainCafeService.class.getName());
    private final IDao<User> dao;

    // Default -> "users.txt"
    public MainCafeService() {
        this.dao = new MyDMFileImpl("users.txt");
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
        return dao.save(new User(username, password));
    }

    public User findUser(String username) {
        return dao.find(username);
    }
}
