package app.Server.service;

import app.Server.dao.IDao;
import app.Server.dao.MyDMFileImpl;
import app.shared.User;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private static final String TEST_FILE = "test_users.txt";

    @BeforeEach
    void setUp() {
        // Ensure a clean file before each run
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }

        IDao<User> userDao = new MyDMFileImpl(TEST_FILE);  // Use MyDMFileImpl for test
        userService = new UserService(userDao);
    }

    @Test
    void testRegisterNewUser() {
        boolean success = userService.register("testUser", "password123");
        assertTrue(success, "User should be registered successfully");
    }

    @Test
    void testAuthenticateValidUser() {
        userService.register("testUser", "password123");
        boolean authenticated = userService.authenticate("testUser", "password123");
        assertTrue(authenticated, "User should authenticate successfully");
    }

    @Test
    void testAuthenticateInvalidUser() {
        boolean authenticated = userService.authenticate("fakeUser", "wrongPass");
        assertFalse(authenticated, "Invalid credentials should fail authentication");
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        new File(TEST_FILE).delete();
    }
}
