package app.server.service;

import java.util.Map;

/**
 * Represents a generic JSON request from the client.
 */
public class Request {
    private Map<String, String> headers;
    private Map<String, String> body;

    public Request() { }

    public Request(Map<String, String> headers, Map<String, String> body) {
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
