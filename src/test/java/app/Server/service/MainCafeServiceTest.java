package app.Server.service;

import app.Server.dao.MyDMFileImpl;
import app.shared.User;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MainCafeServiceTest {
    private MyDMFileImpl userDao;
    private static final String TEST_FILE = "test_users.txt";

    @BeforeEach
    void setUp() {
        deleteTestFile();  // Ensure a clean test environment
        userDao = new MyDMFileImpl();
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
        deleteTestFile();
    }

    private void deleteTestFile() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
