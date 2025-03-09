package app.server.service;

/**
 * Simple driver class to start the server in its own main method.
 */
public class ServerDriver {
    public static void main(String[] args) {
        // Start server on port 34567
        Server server = new Server(34567);
        new Thread(server).start();
        System.out.println("Server started on port 34567...");
    }
}
