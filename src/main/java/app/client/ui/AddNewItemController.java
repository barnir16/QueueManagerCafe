package app.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for AddNewItem.fxml
 * Allows user to input a new item name & weight,
 * then calls mainController.addItemToMenu(...)
 */
public class AddNewItemController {

    @FXML private TextField itemNameField;
    @FXML private TextField itemWeightField;

    private MainCafeController mainController;

    /**
     * Called by MainCafeController to pass a reference to itself,
     * so we can update the menu with the newly added item.
     */
    public void setMainController(MainCafeController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleAdd() {
        String name = itemNameField.getText().trim();
        String weightStr = itemWeightField.getText().trim();

        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty");
            return;
        }

        try {
            int weight = Integer.parseInt(weightStr);
            if (weight <= 0) {
                System.out.println("Weight must be a positive integer");
                return;
            }
            if (mainController != null) {
                mainController.addItemToMenu(name, weight);
            } else {
                System.out.println("No mainController set; cannot add item to menu.");
            }
            close();
        } catch (NumberFormatException ex) {
            System.out.println("Invalid weight, must be numeric");
        }
    }

    @FXML
    private void handleClose() {
        close();
    }

    private void close() {
        Stage stage = (Stage) itemNameField.getScene().getWindow();
        stage.close();
    }
}
