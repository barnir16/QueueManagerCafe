package app.server.service;

import app.server.dao.MyDMFileImpl;
import app.shared.User;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MainCafeServiceTest {
    private MyDMFileImpl userDao;
    private static final String TEST_FILE = "test_users.txt";

    @BeforeEach
    void setUp() {
        // forcibly delete test_users.txt so it’s guaranteed empty
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        // now create a MyDMFileImpl with that test file
        userDao = new MyDMFileImpl(TEST_FILE);
    }

    @Test
    void testSaveNewUser() {
        User user = new User("testUser", "password123");
        boolean success = userDao.save(user);
        assertTrue(success, "User should be saved successfully");
    }

    @Test
    void testSaveDuplicateUser() {
        User user = new User("dupUser", "password123");
        userDao.save(user);
        boolean success = userDao.save(user);
        assertFalse(success, "Duplicate user should not be saved");
    }

    @Test
    void testFindUser() {
        User user = new User("findUser", "password123");
        userDao.save(user);
        assertNotNull(userDao.find("findUser"), "User should be found");
    }

    @AfterEach
    void tearDown() {
        // optional: delete test file again
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
