package app.shared;

import java.io.Serializable;

/**
 * A simple User model with username & password.
 */
public class User implements Serializable {
    private String username;
    private String password;

    public User() {
        // No-arg constructor if needed by JSON
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User{username='" + username + "'}";
    }
}
