package app.server.service; // or similar

public class Response {
    private String message;

    // No-arg constructor for JSON deserialization
    public Response() {
    }

    public Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
