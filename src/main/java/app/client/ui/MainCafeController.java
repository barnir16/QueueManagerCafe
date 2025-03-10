package app.client.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Controller for MainCafe.fxml.
 * - cartMap: holds item -> count for the "cart".
 * - queueList: list of string orders in the queue.
 * - processedList: list of string orders that were processed.
 * - itemWeights: map of itemName -> weight, used if you want weight logic.
 *
 * This class references other controllers by loading their FXML
 * and passing "this" (the MainCafeController) so those controllers can call back methods here.
 */
public class MainCafeController {

    @FXML private Label currentAlgorithmLabel;
    @FXML private VBox menuBox;

    @FXML private ListView<String> cartView;
    @FXML private ListView<String> queueView;
    @FXML private ListView<String> processedOrdersView;

    // Items currently in the cart: itemName -> count
    private final Map<String, Integer> cartMap = new HashMap<>();

    // Each "place order" produces a simple string; stored in queueList
    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    // Processed orders stored here
    private final ObservableList<String> processedList = FXCollections.observableArrayList();

    // itemName -> weight
    private final Map<String, Integer> itemWeights = new HashMap<>();

    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        currentAlgorithmLabel.setText("Current Algorithm: Time Based Algorithm");

        // Bind the queueView & processedOrdersView to our observable lists
        queueView.setItems(queueList);
        processedOrdersView.setItems(processedList);

        // Optionally start in normal or dark mode
        Platform.runLater(() -> {
            // applyDarkMode(false); // if you want normal by default
        });
    }

    // ----------------------------------------------------------------
    //  PLUS / MINUS item logic (existing cappuccino, espresso, sandwich)
    // ----------------------------------------------------------------
    @FXML
    private void handleItemPlus(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String itemName = (String) btn.getUserData();
        cartMap.put(itemName, cartMap.getOrDefault(itemName, 0) + 1);
        refreshCartView();
    }

    @FXML
    private void handleItemMinus(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String itemName = (String) btn.getUserData();
        int count = cartMap.getOrDefault(itemName, 0);
        if (count > 0) {
            count--;
            if (count == 0) {
                cartMap.remove(itemName);
            } else {
                cartMap.put(itemName, count);
            }
            refreshCartView();
        }
    }

    private void refreshCartView() {
        cartView.getItems().clear();
        for (Map.Entry<String, Integer> e : cartMap.entrySet()) {
            cartView.getItems().add(e.getKey() + " x" + e.getValue());
        }
    }

    // ----------------------------------------------------------------
    //  ADD a brand-new item to the left menu dynamically
    // ----------------------------------------------------------------
    public void addItemToMenu(String name, int weight) {
        if (itemWeights.containsKey(name)) {
            showInfo("Item already exists: " + name);
            return;
        }
        itemWeights.put(name, weight);

        // Create label + plus/minus buttons
        Label itemLabel = new Label(name);
        itemLabel.setStyle("-fx-text-fill: #ecf0f1;");

        Button plusButton = new Button("+");
        plusButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        plusButton.setUserData(name);
        plusButton.setOnAction(this::handleItemPlus);

        Button minusButton = new Button("-");
        minusButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        minusButton.setUserData(name);
        minusButton.setOnAction(this::handleItemMinus);

        HBox itemBox = new HBox(10, itemLabel, plusButton, minusButton);

        // Insert above the last 3 big buttons (Add/Remove/Change Weight).
        int insertIndex = Math.max(0, menuBox.getChildren().size() - 3);
        menuBox.getChildren().add(insertIndex, itemBox);

        System.out.println("Added item to menu: " + name + " (weight=" + weight + ")");
    }

    // ----------------------------------------------------------------
    //  REMOVE an existing item from the left menu
    // ----------------------------------------------------------------
    public void removeItemFromMenu(String name) {
        // Remove from itemWeights
        itemWeights.remove(name);

        // Also remove from cartMap if present
        cartMap.remove(name);
        refreshCartView();

        // Now remove from the UI: find an HBox with a label that has text == name
        var children = menuBox.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof HBox hbox && !hbox.getChildren().isEmpty()) {
                if (hbox.getChildren().get(0) instanceof Label lbl) {
                    if (lbl.getText().equals(name)) {
                        children.remove(i);
                        break;
                    }
                }
            }
        }
        System.out.println("Removed item from menu: " + name);
    }

    // ----------------------------------------------------------------
    //  BOTTOM ROW: Place / Process / Clear
    // ----------------------------------------------------------------
    @FXML
    private void handlePlaceOrder() {
        if (cartMap.isEmpty()) {
            showInfo("Cart is empty, cannot place an order!");
            return;
        }
        // Build an order string
        StringBuilder sb = new StringBuilder("Order #");
        sb.append(queueList.size() + 1).append(": [");

        boolean first = true;
        for (Map.Entry<String, Integer> e : cartMap.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append(" x").append(e.getValue());
            first = false;
        }
        sb.append("]");

        // Add to queue
        queueList.add(sb.toString());

        // Clear cart
        cartMap.clear();
        refreshCartView();
    }

    @FXML
    private void handleProcessOrder() {
        if (!queueList.isEmpty()) {
            String order = queueList.remove(0);
            processedList.add(order + " [Processed]");
        } else {
            showInfo("Queue is empty, nothing to process!");
        }
    }

    @FXML
    private void handleClearProcessed() {
        processedList.clear();
    }

    // ----------------------------------------------------------------
    //  BIG 3 LEFT BUTTONS
    // ----------------------------------------------------------------
    @FXML
    private void handleOpenAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/AddNewItem.fxml"));
            Scene scene = new Scene(loader.load());

            // If in dark mode, add dark-mode
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            AddNewItemController controller = loader.getController();
            controller.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Add New Item");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open AddNewItem.fxml\n" + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/RemoveItem.fxml"));
            Scene scene = new Scene(loader.load());

            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            RemoveItemController controller = loader.getController();
            controller.setMainController(this);
            controller.initComboBox(new ArrayList<>(itemWeights.keySet()));

            Stage dialog = new Stage();
            dialog.setTitle("Remove Item");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open RemoveItem.fxml\n" + e.getMessage());
        }
    }

    @FXML
    private void handleOpenChangeWeight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/ChangeItemWeight.fxml"));
            Scene scene = new Scene(loader.load());

            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            ChangeItemWeightController controller = loader.getController();
            controller.setMainController(this);
            controller.initItems(new ArrayList<>(itemWeights.keySet()));

            Stage dialog = new Stage();
            dialog.setTitle("Change Item Weight");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open ChangeItemWeight.fxml\n" + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  BOTTOM ROW: Toggle Theme / Time Values / Weights / Algorithm
    // ----------------------------------------------------------------
    @FXML
    private void handleToggleTheme() {
        isDarkMode = !isDarkMode;
        applyDarkMode(isDarkMode);
    }

    @FXML
    private void handleTimeValues() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SetTimeValues.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            SetTimeValuesController controller = loader.getController();
            controller.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Set Time Values");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SetTimeValues.fxml\n" + e.getMessage());
        }
    }

    @FXML
    private void handleTimeWeights() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SetTimeWeights.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            SetTimeWeightsController controller = loader.getController();
            controller.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Set Time Weights");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SetTimeWeights.fxml\n" + e.getMessage());
        }
    }

    @FXML
    private void handleSelectAlgorithm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SelectAlg.fxml"));
            Scene scene = new Scene(loader.load());
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());
            }

            SelectAlgController controller = loader.getController();
            controller.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Select Algorithm");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SelectAlg.fxml\n" + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  PUBLIC METHOD to update the algorithm label
    // ----------------------------------------------------------------
    public void updateAlgorithm(String algorithmName) {
        // Safely update the label inside MainCafeController
        currentAlgorithmLabel.setText("Current Algorithm: " + algorithmName);
    }

    // ----------------------------------------------------------------
    //  UTILITY
    // ----------------------------------------------------------------
    private void applyDarkMode(boolean enable) {
        Scene mainScene = menuBox.getScene();
        if (mainScene == null) return;

        var darkCss = getClass().getResource("/css/dark-mode.css");
        if (darkCss == null) {
            System.err.println("dark-mode.css not found in /css/ folder!");
            return;
        }
        String darkPath = darkCss.toExternalForm();

        if (enable) {
            if (!mainScene.getStylesheets().contains(darkPath)) {
                mainScene.getStylesheets().add(darkPath);
            }
        } else {
            mainScene.getStylesheets().remove(darkPath);
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // For ChangeItemWeightController usage:
    public int getWeightForItem(String item) {
        return itemWeights.getOrDefault(item, 1);
    }

    public void setWeightForItem(String item, int newWeight) {
        if (itemWeights.containsKey(item)) {
            itemWeights.put(item, newWeight);
        }
    }
}
