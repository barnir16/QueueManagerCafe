package app.server.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HandleRequestTest {
    private HandleRequest handleRequest;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        handleRequest = new HandleRequest(null); // No socket needed for unit testing
    }

    @Test
    void testSaveUserRequest() throws Exception {
        Method method = HandleRequest.class.getDeclaredMethod("processRequest", Request.class);
        method.setAccessible(true); // Allow access to protected/private method

        // Prepare request
        Map<String, String> headers = new HashMap<>();
        headers.put("action", "saveUser");  // ✅ Matches HandleRequest action

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
        method.setAccessible(true); // Allow access to protected/private method

        // First, save the user
        testSaveUserRequest();

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
        method.setAccessible(true); // Allow access to protected/private method

        Map<String, String> headers = new HashMap<>();
        headers.put("action", "findUser");

        Map<String, String> body = new HashMap<>();
        body.put("username", "unknownUser");

        Request request = new Request(headers, body);
        Response response = (Response) method.invoke(handleRequest, request);

        assertEquals("NOT FOUND", response.getMessage(), "User should not be found");
    }
}
