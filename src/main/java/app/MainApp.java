package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApp is the single entry point for the entire application.
 * It first loads the Login scene (Login.fxml).
 * Once the user logs in successfully, the LoginController
 * will call MainApp.showMainCafe() to switch to MainCafe.fxml.
 */
public class MainApp extends Application {

    private static Stage primaryStage;  // We keep a static reference for convenience

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLogin();
    }

    /**
     * Displays the Login window (Login.fxml).
     */
    private void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/Login.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            primaryStage.setTitle("Cafe - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading Login.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called by LoginController upon successful login.
     * This method switches the scene to MainCafe.fxml.
     */
    public static void showMainCafe() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/app/client/ui/MainCafe.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 600);
            primaryStage.setTitle("Cafe Management");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading MainCafe.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // If you want to start the server in the same process (optional):
        // new Thread(new Server(34567)).start();
        // Typically, you'd run ServerDriver in a separate process.

        launch(args); // Launches JavaFX
    }
}
