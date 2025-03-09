package app.server.service;
import app.server.dao.MyDMFileImpl;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HandleRequestTest {
    private HandleRequest handleRequest;
    private Gson gson;
    private static final String TEST_FILE = "test_users.txt";

    @BeforeEach
    void setUp() throws Exception {
        // 1) Delete test file so each test is fresh
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }

        // 2) Create a new MyDMFileImpl that references test_users.txt
        MyDMFileImpl dao = new MyDMFileImpl(TEST_FILE);

        // 3) Create a MainCafeService with that test file
        MainCafeService testService = new MainCafeService(dao);

        // 4) Inject that service into HandleRequest
        handleRequest = new HandleRequest(null, testService);

        // 5) GSON init
        gson = new Gson();
    }

    @Test
    void testSaveUserRequest() throws Exception {
        Method method = HandleRequest.class.getDeclaredMethod("processRequest", Request.class);
        method.setAccessible(true); // Allow access to private method

        // Prepare request
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "saveUser");

        Map<String, String> body = new HashMap<>();
        body.put("username", "testUser");
        body.put("password", "password123");

        Request request = new Request(headers, body);
        Response response = (Response) method.invoke(handleRequest, request);

        assertEquals("SAVED", response.getMessage(), "User should be saved successfully");
    }

    @Test
    void testFindUserRequest() throws Exception {
        Method method = HandleRequest.class.getDeclaredMethod("processRequest", Request.class);
        method.setAccessible(true);

        // First, save the user
        testSaveUserRequest();  // reuses the test above

        // Prepare request to find user
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "findUser");

        Map<String, String> body = new HashMap<>();
        body.put("username", "testUser");

        Request request = new Request(headers, body);
        Response response = (Response) method.invoke(handleRequest, request);

        assertTrue(response.getMessage().contains("FOUND"), "User should be found");
    }

    @Test
    void testFindNonExistingUserRequest() throws Exception {
        Method method = HandleRequest.class.getDeclaredMethod("processRequest", Request.class);
        method.setAccessible(true);

        // Attempt to find a user that doesn't exist
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "findUser");

        Map<String, String> body = new HashMap<>();
        body.put("username", "unknownUser");

        Request request = new Request(headers, body);
        Response response = (Response) method.invoke(handleRequest, request);

        assertEquals("NOT FOUND", response.getMessage(), "User should not be found");
    }
}
