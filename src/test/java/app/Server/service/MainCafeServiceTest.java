package app.server.service;

import app.server.dao.MyDMFileImpl;
import app.shared.User;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the MainCafeService class with a file-based DAO.
 */
public class MainCafeServiceTest {
    private static final String TEST_FILE = "test_users.txt";
    private MainCafeService service;

    @BeforeEach
    void setUp() {
        MyDMFileImpl<User> dao = new MyDMFileImpl<>(TEST_FILE);
        service = new MainCafeService(dao);
    }

    @Test
    void testSaveNewUser() {
        boolean success = service.saveUser("testUser", "password123");
        assertTrue(success, "User should be saved successfully");
    }

    @Test
    void testSaveDuplicateUser() {
        service.saveUser("dupUser", "pass1");
        boolean success = service.saveUser("dupUser", "pass2");
        assertFalse(success, "Saving duplicate user should fail");
    }

    @Test
    void testFindUser() {
        service.saveUser("findMe", "secret");
        User found = service.findUser("findMe");
        assertNotNull(found, "Should find the user we just saved");
        assertEquals("findMe", found.getUsername());
        assertEquals("secret", found.getPassword());
    }

    @AfterEach
    void tearDown() {
        // Clean up test file
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
