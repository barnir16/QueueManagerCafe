package app.server.service;

/**
 * Represents a JSON response sent back to the client.
 */
public class Response {
    private String message;

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
