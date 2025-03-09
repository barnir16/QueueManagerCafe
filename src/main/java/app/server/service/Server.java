package app.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main server class. Listens for connections and delegates to HandleRequest.
 */
public class Server implements Runnable {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private final ExecutorService executorService;

    public Server(int port) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port " + port);

            while (true) {
                executorService.execute(new HandleRequest(serverSocket.accept()));
                logger.info("New client connected.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server failed to start on port " + port, e);
        }
    }
}
