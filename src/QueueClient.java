import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * QueueClient - Connects to the QueueServer and sends example requests.
 */
public class QueueClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", QueueServer.PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Connected to the server!");
            output.writeObject("TEST ORDER");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
