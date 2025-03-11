package app.client.ui;

import algorithm.*;
import model.Order;
import queue.IQueueAlgorithm;
import queue.OrderQueue;

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
import java.time.LocalDateTime;
import java.util.*;

/**
 * A single controller that handles:
 * - (Optional) login fields
 * - The main cafe logic (cart, items, queue, algorithm switching, etc.)
 */
public class ClientUIController {

    // ----------------------------------------
    // (Optional) LOGIN-RELATED FIELDS
    // ----------------------------------------
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label responseLabel;

    // ----------------------------------------
    // MAIN CAFE FIELDS
    // ----------------------------------------
    @FXML private Label currentAlgorithmLabel;
    @FXML private VBox menuBox;            // The left panel container
    @FXML private ListView<String> cartView;
    @FXML private ListView<String> queueView;
    @FXML private ListView<String> processedOrdersView;
    @FXML private CheckBox memberCheckBox;

    // Data structures
    private final Map<String, Integer> cartMap        = new HashMap<>();
    private final ObservableList<String> queueList     = FXCollections.observableArrayList();
    private final ObservableList<String> processedList = FXCollections.observableArrayList();
    private final Map<String, Integer> itemWeights     = new HashMap<>();

    private OrderQueue      cafeQueue;
    private IQueueAlgorithm currentAlg;

    private int     nextOrderId    = 1;
    private boolean isDarkMode     = false;

    // If you want to store default thresholds/weights even if user is not time-based:
    private int[] storedTimeThresholds = {5, 10};
    private int[] storedTimeWeights    = {1, 2, 3};

    // ----------------------------------------
    // INITIALIZE
    // ----------------------------------------
    @FXML
    public void initialize() {
        // Default algorithm
        currentAlg = new TimeBasedAlgorithm();
        cafeQueue  = new OrderQueue(currentAlg);

        // Set label text if it exists
        if (currentAlgorithmLabel != null) {
            currentAlgorithmLabel.setText("Current Algorithm: Time Based");
        }

        // Bind queue & processed lists to UI
        if (queueView != null) {
            queueView.setItems(queueList);
        }
        if (processedOrdersView != null) {
            processedOrdersView.setItems(processedList);
        }

        // Example default items
        itemWeights.put("Cappuccino", 2);
        itemWeights.put("Espresso",   1);
        itemWeights.put("Sandwich",   3);

        // Optionally start in "light" mode
        Platform.runLater(() -> {
            applyDarkMode(false);
        });
    }

    // ----------------------------------------
    // ALGORITHM SWITCH
    // ----------------------------------------
    public void updateAlgorithm(String algName) {
        IQueueAlgorithm newAlg;
        switch (algName) {
            case "Batch Item":
                BatchItemAlgorithm bia = new BatchItemAlgorithm();
                // Example: set batch vs. individual
                bia.setWeights(3, 1);
                newAlg = bia;
                break;

            case "Member Priority":
                newAlg = new MemberPriorityAlgorithm();
                break;

            case "Item Weight":
                ItemWeightAlgorithm iwa = new ItemWeightAlgorithm();
                // Set item weights so the JAR sees them
                iwa.setItemWeight("Cappuccino", 2);
                iwa.setItemWeight("Espresso",   1);
                iwa.setItemWeight("Sandwich",   3);
                newAlg = iwa;
                break;

            default: // "Time Based"
                newAlg = new TimeBasedAlgorithm();
                break;
        }
        currentAlg = newAlg;
        cafeQueue.setAlgorithm(newAlg);

        if (currentAlgorithmLabel != null) {
            currentAlgorithmLabel.setText("Current Algorithm: " + algName);
        }
        System.out.println("Switched algorithm to: " + algName);

        refreshQueueView();
    }

    // ----------------------------------------
    // CART PLUS/MINUS
    // ----------------------------------------
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
        if (cartView == null) return;
        cartView.getItems().clear();
        cartMap.forEach((name, qty) -> {
            cartView.getItems().add(name + " x" + qty);
        });
    }

    // ----------------------------------------
    // ADD / REMOVE MENU ITEMS DYNAMICALLY
    // ----------------------------------------
    public void addItemToMenu(String name, int weight) {
        if (itemWeights.containsKey(name)) {
            showInfo("Item already exists: " + name);
            return;
        }
        itemWeights.put(name, weight);

        Label lbl = new Label(name);
        // If you want specific styling for new items, e.g.:
        // lbl.getStyleClass().add("label");

        Button plusBtn = new Button("+");
        plusBtn.setUserData(name);
        plusBtn.setOnAction(this::handleItemPlus);

        Button minusBtn = new Button("-");
        minusBtn.setUserData(name);
        minusBtn.setOnAction(this::handleItemMinus);

        HBox buttonRow = new HBox(10, plusBtn, minusBtn);

        if (menuBox == null) {
            System.err.println("menuBox is null - cannot add item UI.");
            return;
        }
        // Insert them above the last 3 big buttons in the left menu
        int insertIndex = Math.max(0, menuBox.getChildren().size() - 1 /* the sub-VBox is 1 node? or 4? */);
        // Actually we have a sub-VBox with the membership & order buttons as the last node.
        // So let's subtract 1 from the total. If you have more nodes, adjust accordingly.
        menuBox.getChildren().add(insertIndex, lbl);
        menuBox.getChildren().add(insertIndex + 1, buttonRow);

        System.out.println("Added item: " + name + " weight=" + weight);
    }

    public void removeItemFromMenu(String name) {
        itemWeights.remove(name);
        cartMap.remove(name);
        refreshCartView();

        if (menuBox == null) return;
        var children = menuBox.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof Label lbl && lbl.getText().equals(name)) {
                // remove label
                children.remove(i);
                // remove next node if it's an HBox
                if (i < children.size() && (children.get(i) instanceof HBox)) {
                    children.remove(i);
                }
                System.out.println("Removed item from menu: " + name);
                return;
            }
        }
        System.out.println("Item '" + name + "' not found in the menuBox UI.");
    }

    // ----------------------------------------
    // PLACE / PROCESS / CLEAR
    // ----------------------------------------
    @FXML
    private void handlePlaceOrder() {
        if (cartMap.isEmpty()) {
            showInfo("Cart is empty, cannot place an order!");
            return;
        }
        Map<String, Integer> itemsCopy = new HashMap<>(cartMap);
        boolean isMember = (memberCheckBox != null && memberCheckBox.isSelected());
        Order newOrder = new Order(nextOrderId++, itemsCopy, isMember);
        newOrder.setTimePlaced(LocalDateTime.now());

        cafeQueue.addOrder(newOrder);
        cartMap.clear();
        refreshCartView();
        refreshQueueView();
    }

    @FXML
    private void handleProcessOrder() {
        Order processed = cafeQueue.processNextOrder();
        if (processed != null) {
            processed.setTimeProcessed(LocalDateTime.now());
            processedList.add(processed.toString());
            refreshQueueView();
        } else {
            showInfo("Queue is empty!");
        }
    }

    @FXML
    private void handleClearProcessed() {
        processedList.clear();
    }

    private void refreshQueueView() {
        if (queueView == null) return;
        queueList.clear();
        for (Order o : cafeQueue.getQueue()) {
            queueList.add(o.toString());
        }
    }

    // ----------------------------------------
    // THEME TOGGLE + SUBWINDOWS
    // ----------------------------------------
    @FXML
    private void handleToggleTheme() {
        isDarkMode = !isDarkMode;
        applyDarkMode(isDarkMode);
    }

    /**
     * Toggles between coffee-light.css (light mode) and coffee-dark.css (dark mode).
     */
    private void applyDarkMode(boolean enable) {
        if (menuBox == null || menuBox.getScene() == null) return;
        Scene mainScene = menuBox.getScene();

        // We'll remove both possible styles, then add whichever is correct
        String lightCss = getClass().getResource("/css/coffee-light.css").toExternalForm();
        String darkCss  = getClass().getResource("/css/coffee-dark.css").toExternalForm();
        mainScene.getStylesheets().removeAll(lightCss, darkCss);

        if (enable) {
            mainScene.getStylesheets().add(darkCss);
        } else {
            mainScene.getStylesheets().add(lightCss);
        }
    }

    /**
     * Applies the same stylesheet to a newly opened subwindow scene
     * so that popups also share dark/light mode.
     */
    private void addSceneStylesheet(Scene scene) {
        String lightCss = getClass().getResource("/css/coffee-light.css").toExternalForm();
        String darkCss  = getClass().getResource("/css/coffee-dark.css").toExternalForm();

        // If we're in dark mode, add dark; else add light
        if (isDarkMode) {
            scene.getStylesheets().add(darkCss);
        } else {
            scene.getStylesheets().add(lightCss);
        }
    }

    // ----------------------------------------
    // TIME VALUES / WEIGHTS / ALGORITHM
    // Always accessible, even if currentAlg isn't time-based
    // ----------------------------------------
    @FXML
    private void handleTimeValues() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/SetTimeValues.fxml"));
            Scene scene = new Scene(loader.load());
            addSceneStylesheet(scene);

            SetTimeValuesController ctrl = loader.getController();
            ctrl.setMainController(this);

            // If currentAlg is time-based, show actual thresholds; else show stored defaults
            if (currentAlg instanceof TimeWeightedAlgorithm twa) {
                ctrl.initValues(twa.getTimeThresholds());
            } else {
                ctrl.initValues(storedTimeThresholds);
            }

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
            addSceneStylesheet(scene);

            SetTimeWeightsController ctrl = loader.getController();
            ctrl.setMainController(this);

            // If currentAlg is time-based, show actual weights; else show stored defaults
            if (currentAlg instanceof TimeWeightedAlgorithm twa) {
                ctrl.initWeights(twa.getWeights());
            } else {
                ctrl.initWeights(storedTimeWeights);
            }

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
            addSceneStylesheet(scene);

            SelectAlgController ctrl = loader.getController();
            ctrl.setMainController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Select Algorithm");
            dialog.setScene(scene);
            dialog.show();
        } catch (IOException e) {
            showError("Failed to open SelectAlg.fxml\n" + e.getMessage());
        }
    }

    // ----------------------------------------
    // "Add / Remove / Change Weight" DIALOGS
    // (the ones referenced in the bottom bar)
    // ----------------------------------------
    @FXML
    private void handleOpenAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/client/ui/AddNewItem.fxml"));
            Scene scene = new Scene(loader.load());
            addSceneStylesheet(scene);

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
            addSceneStylesheet(scene);

            RemoveItemController controller = loader.getController();
            controller.setMainController(this);
            // Populate the combo box with current item names
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
            addSceneStylesheet(scene);

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

    // ----------------------------------------
    // UTILITY
    // ----------------------------------------
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

    public IQueueAlgorithm getCurrentAlgorithm() {
        return currentAlg;
    }

    // For "ChangeItemWeightController" usage
    public int getWeightForItem(String item) {
        return itemWeights.getOrDefault(item, 1);
    }

    public void setWeightForItem(String item, int newWeight) {
        if (itemWeights.containsKey(item)) {
            itemWeights.put(item, newWeight);
        }
    }

    // If the user *changes* time values/weights while non-time-based,
    // we store them here. If they switch to time-based later, you could
    // reapply them. (Up to you.)
    public void updateStoredTimeThresholds(int low, int mid) {
        storedTimeThresholds[0] = low;
        storedTimeThresholds[1] = mid;

        // If we *are* time-based, also set them now:
        if (currentAlg instanceof TimeWeightedAlgorithm twa) {
            twa.setTimeThresholds(low, mid);
        }
    }

    public void updateStoredTimeWeights(int low, int mid, int high) {
        storedTimeWeights[0] = low;
        storedTimeWeights[1] = mid;
        storedTimeWeights[2] = high;

        if (currentAlg instanceof TimeWeightedAlgorithm twa) {
            twa.setWeights(low, mid, high);
        }
    }

    // ----------------------------------------
    // EXAMPLE: If you merged your login code here
    // ----------------------------------------
    @FXML
    private void handleLogin() {
        System.out.println("Login logic here (request to server, etc.)");
    }

    @FXML
    private void handleRegister() {
        System.out.println("Register logic here (request to server, etc.)");
    }
}
