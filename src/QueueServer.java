import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * QueueServer - Starts a server for handling client requests.
 */
public class QueueServer {
    public static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
