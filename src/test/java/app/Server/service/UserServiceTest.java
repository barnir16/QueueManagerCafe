package app.Server.service;

import app.server.dao.UserDaoImpl;
import app.server.service.UserService;
import app.shared.User;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserDaoImpl userDao = new UserDaoImpl();  // Use UserDaoImpl instead of MyDMFileImpl
        userService = new UserService(userDao);   // Now matches constructor
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
        new File("users.txt").delete(); // Clean up the user file after each test
    }
}
