package app.Server.service;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private static final int TEST_PORT = 34567;
    private ServerSocket serverSocket;

    @BeforeEach
    void setUp() {
        try {
            serverSocket = new ServerSocket(TEST_PORT);
        } catch (IOException e) {
            fail("Failed to start test server on port " + TEST_PORT);
        }
    }

    @Test
    void testServerStartsCorrectly() {
        assertNotNull(serverSocket, "Server should start successfully on the test port");
    }

    @AfterEach
    void tearDown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            fail("Failed to close test server");
        }
    }
}
