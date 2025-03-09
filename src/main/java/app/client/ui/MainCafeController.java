package app.client.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.SplitPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;

/**
 * Controller for MainCafe.fxml
 */
public class MainCafeController {

    @FXML private Label currentAlgorithmLabel;
    @FXML private VBox menuBox;

    @FXML private SplitPane verticalSplitPane;      // We set orientation in code
    @FXML private ListView<String> cartView;
    @FXML private ListView<String> queueView;
    @FXML private ListView<String> processedOrdersView;

    private final ObservableList<String> cartList = FXCollections.observableArrayList();
    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    private final ObservableList<String> processedList = FXCollections.observableArrayList();

    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        // Set orientation in code, so no "orientation" attribute in FXML
        verticalSplitPane.setOrientation(Orientation.VERTICAL);

        // Bind data to the ListViews
        cartView.setItems(cartList);
        queueView.setItems(queueList);
        processedOrdersView.setItems(processedList);

        currentAlgorithmLabel.setText("Current Algorithm: Time Based Algorithm");
    }

    @FXML
    private void handleItemPlus(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        String itemName = (String) sourceBtn.getUserData();
        cartList.add(itemName);
    }

    @FXML
    private void handleItemMinus(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        String itemName = (String) sourceBtn.getUserData();
        cartList.remove(itemName);
    }

    @FXML
    private void handleOpenAddItem() {
        showInfo("Add New Item: open 'Add new Item.fxml' or show a dialog.");
    }

    @FXML
    private void handleRemoveItem() {
        showInfo("Remove Item: not implemented yet.");
    }

    @FXML
    private void handleOpenChangeWeight() {
        showInfo("Change Weight: open 'Change Item weight.fxml' if you wish.");
    }

    @FXML
    private void handlePlaceOrder() {
        if (cartList.isEmpty()) {
            showInfo("Cart is empty, cannot place an order!");
            return;
        }
        String order = "Order #" + (queueList.size() + 1) + ": " + cartList;
        queueList.add(order);
        cartList.clear();
        showInfo("Placed new order: " + order);
    }

    @FXML
    private void handleProcessOrder() {
        if (!queueList.isEmpty()) {
            String nextOrder = queueList.remove(0);
            processedList.add(nextOrder + " [Processed]");
            showInfo("Processed: " + nextOrder);
        } else {
            showInfo("Queue is empty, nothing to process!");
        }
    }

    @FXML
    private void handleClearProcessed() {
        processedList.clear();
        showInfo("Cleared all processed orders!");
    }

    @FXML
    private void handleToggleTheme() {
        Node root = menuBox.getScene().getRoot();
        if (isDarkMode) {
            root.getStyleClass().remove("dark-mode");
        } else {
            root.getStyleClass().add("dark-mode");
        }
        isDarkMode = !isDarkMode;
    }

    @FXML
    private void handleTimeValues() {
        showInfo("Set Time Values not implemented yet!");
    }

    @FXML
    private void handleTimeWeights() {
        showInfo("Set Time Weights not implemented yet!");
    }

    @FXML
    private void handleSelectAlgorithm() {
        showInfo("Switch Algorithm not implemented yet!");
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Message");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
