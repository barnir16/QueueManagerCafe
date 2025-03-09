package app.Server.service;

/**
 * Main driver to start the server.
 */
public class ServerDriver {
    public static void main(String[] args) {
        // Start server on port 34567
        Server server = new Server(34567);
        new Thread(server).start();
    }
}
