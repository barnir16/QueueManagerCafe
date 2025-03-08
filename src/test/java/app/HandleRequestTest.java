package app;

import app.server.service.HandleRequest;
import app.server.service.Request;
import app.server.service.Response;
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
        handleRequest = new HandleRequest(null); // Null socket since we're only testing logic
    }

    @Test
    void testLoginRequest() throws Exception {
        Method method = HandleRequest.class.getDeclaredMethod("processRequest", Request.class);
        method.setAccessible(true); // Allow access to the protected method

        Map<String, String> headers = new HashMap<>();
        headers.put("action", "LOGIN");

        Map<String, String> body = new HashMap<>();
        body.put("username", "testUser");
        body.put("password", "password123");

        Request request = new Request(headers, body);
        Response response = (Response) method.invoke(handleRequest, request);

        assertEquals("FAILED", response.getMessage(), "User should not be authenticated initially");
    }

    @Test
    void testRegisterRequest() throws Exception {
        Method method = HandleRequest.class.getDeclaredMethod("processRequest", Request.class);
        method.setAccessible(true); // Allow access to the protected method

        Map<String, String> headers = new HashMap<>();
        headers.put("action", "REGISTER");

        Map<String, String> body = new HashMap<>();
        body.put("username", "newUser");
        body.put("password", "newPass");

        Request request = new Request(headers, body);
        Response response = (Response) method.invoke(handleRequest, request);

        assertEquals("REGISTERED", response.getMessage(), "User should be registered successfully");
    }
}
