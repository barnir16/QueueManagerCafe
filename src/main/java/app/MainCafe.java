package app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import queue.IQueueAlgorithm;
import queue.OrderQueue;
import algorithm.TimeBasedAlgorithm;
import model.Order;
import algorithm.*;
import app.client.utils.OrderLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


public class MainCafe extends Application {
    private static int currentOrderId = 1;
    private boolean isDarkMode = false;

    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    private final ObservableList<String> processedOrdersList = FXCollections.observableArrayList();
    private final Map<String, Integer> currentOrder = new HashMap<>();
    private final Map<String, Integer> itemWeights = new HashMap<>();
    private final ObservableList<String> menuItems = FXCollections.observableArrayList("Cappuccino", "Espresso", "Sandwich", "Muffin");
    private final List<Order> allOrders = new ArrayList<>(); // Global list of all orders

    private IQueueAlgorithm algorithm = new TimeBasedAlgorithm(); // Default algorithm
    private final TimeWeightedAlgorithm timeWeightedAlgorithm = new TimeWeightedAlgorithm(); // Always available
    private OrderQueue cafeQueue = new OrderQueue(algorithm);

    private CheckBox memberCheckBox; // Declare it globally
    private Label currentAlgorithmLabel;
    private VBox mainLayout;
    private VBox menuBox;
    private ListView<String> cartView;

    @Override
    public void start(Stage primaryStage) {
        cartView = createStyledListView("Your cart is empty");
        ListView<String> queueListView = createStyledListView("Queue is empty");
        queueListView.setItems(queueList);
        ListView<String> processedOrdersView = createStyledListView("No processed orders yet");
        processedOrdersView.setItems(processedOrdersList);

        menuBox = createMenuBox(cartView);
        HBox buttonBox = createButtonBox(cartView, queueListView);

        SplitPane verticalSplitPane = new SplitPane(cartView, queueListView, processedOrdersView);
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.setDividerPositions(0.33, 0.66);

        SplitPane horizontalSplitPane = new SplitPane(menuBox, verticalSplitPane);
        horizontalSplitPane.setDividerPositions(0.25);

        currentAlgorithmLabel = new Label("Current Algorithm: Time Based Algorithm");
        mainLayout = new VBox(10, currentAlgorithmLabel, horizontalSplitPane, buttonBox);

        Scene scene = new Scene(mainLayout, 1000, 600);
        scene.getStylesheets().add("styles/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cafe Order Management");
        primaryStage.show();
    }

    private ListView<String> createStyledListView(String placeholderText) {
        ListView<String> listView = new ListView<>();
        listView.setPlaceholder(new Label(placeholderText));
        return listView;
    }

    private VBox createMenuBox(ListView<String> cartView) {
        VBox menuBox = new VBox(10);
        for (String item : menuItems) {
            itemWeights.putIfAbsent(item, 1);
            menuBox.getChildren().add(createItemBox(item, cartView));
        }

        // Initialize the global memberCheckBox
        memberCheckBox = new CheckBox("Member");
        menuBox.getChildren().add(memberCheckBox);

        Button addItemButton = new Button("Add New Item");
        addItemButton.setOnAction(e -> showAddNewItemDialog(menuBox, cartView, memberCheckBox));

        Button removeItemButton = new Button("Remove Item");
        removeItemButton.setOnAction(e -> removeItemDialog(menuBox));

        Button changeWeightButton = new Button("Change Item Weight");
        changeWeightButton.setOnAction(e -> showChangeItemWeightDialog());

        menuBox.getChildren().addAll(addItemButton, removeItemButton, changeWeightButton);
        return menuBox;
    }


    private HBox createButtonBox(ListView<String> cartView, ListView<String> queueListView) {
        HBox buttonBox = new HBox(10);

        Button placeOrderButton = new Button("Place Order");
        Button processOrderButton = new Button("Process Order");
        Button clearProcessedOrdersButton = new Button("Clear Processed Orders");
        Button toggleThemeButton = new Button("Toggle Theme");
        Button setTimeValuesButton = new Button("Set Time Values");
        Button setTimeWeightsButton = new Button("Set Time Weights");
        Button switchAlgorithmButton = new Button("Switch Algorithm");

        placeOrderButton.setOnAction(e -> placeOrder(cartView, queueListView));
        processOrderButton.setOnAction(e -> processOrder(queueListView));
        clearProcessedOrdersButton.setOnAction(e -> processedOrdersList.clear());
        toggleThemeButton.setOnAction(e -> toggleTheme(mainLayout));
        setTimeValuesButton.setOnAction(e -> showTimeValuesDialog());
        setTimeWeightsButton.setOnAction(e -> showTimeWeightsDialog());
        switchAlgorithmButton.setOnAction(e -> showAlgorithmSelectionDialog());

        // Add buttons in the desired order
        buttonBox.getChildren().addAll(
                placeOrderButton,
                processOrderButton,
                clearProcessedOrdersButton,
                toggleThemeButton,
                setTimeValuesButton,
                setTimeWeightsButton,
                switchAlgorithmButton
        );

        return buttonBox;
    }

    private void showTimeWeightsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Time Weights");

        VBox dialogContent = new VBox(10);
        Label currentWeights = new Label("Current Weights: Low = " + timeWeightedAlgorithm.getWeights()[0] +
                ", Mid = " + timeWeightedAlgorithm.getWeights()[1] +
                ", High = " + timeWeightedAlgorithm.getWeights()[2]);

        TextField lowField = new TextField(String.valueOf(timeWeightedAlgorithm.getWeights()[0]));
        TextField midField = new TextField(String.valueOf(timeWeightedAlgorithm.getWeights()[1]));
        TextField highField = new TextField(String.valueOf(timeWeightedAlgorithm.getWeights()[2]));

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            try {
                int low = Integer.parseInt(lowField.getText());
                int mid = Integer.parseInt(midField.getText());
                int high = Integer.parseInt(highField.getText());

                if (low <= 0 || mid <= 0 || high <= 0) {
                    showErrorDialog("Weights must be positive values!");
                } else {
                    timeWeightedAlgorithm.setWeights(low, mid, high);
                    dialog.close();
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter valid numeric values.");
            }
        });

        dialogContent.getChildren().addAll(currentWeights, lowField, midField, highField, applyButton);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void placeOrder(ListView<String> cartView, ListView<String> queueListView) {
        if (!currentOrder.isEmpty()) {
            try {
                int orderId = currentOrderId++;
                boolean isMember = memberCheckBox.isSelected();

                Order order = new Order(orderId, new HashMap<>(currentOrder), isMember);
                order.setTimePlaced(LocalDateTime.now()); // Set the timePlaced
                cafeQueue.addOrder(order);

                allOrders.add(order); // Add to global list

                // Log all orders
                OrderLogger.logOrders(allOrders);

                // Update the UI
                queueList.clear();
                cafeQueue.getQueue().forEach(o -> queueList.add(o.toString()));
                currentOrder.clear();
                cartView.getItems().clear();
            } catch (Exception ex) {
                showErrorDialog("Error placing order: " + ex.getMessage());
            }
        } else {
            showErrorDialog("No items in the cart to place an order!");
        }
    }

    private void processOrder(ListView<String> queueListView) {
        Order processedOrder = cafeQueue.processNextOrder();
        if (processedOrder != null) {
            processedOrder.setTimeProcessed(LocalDateTime.now()); // Set processing time

            // Add to processed orders list for display
            processedOrdersList.add(processedOrder.toString());

            // Log all current and processed orders
            List<Order> allOrders = new ArrayList<>(cafeQueue.getQueue());
            allOrders.add(processedOrder); // Ensure the processed order is included in the log
            OrderLogger.logOrders(allOrders);

            // Update the UI
            queueList.clear();
            cafeQueue.getQueue().forEach(o -> queueList.add(o.toString()));
        } else {
            System.out.println("After processing: No more orders."); // Terminal message
        }
    }

    private void showAlgorithmSelectionDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Algorithm");

        VBox dialogContent = new VBox(10);
        ToggleGroup group = new ToggleGroup();

        RadioButton timeBasedButton = new RadioButton("Time Based Algorithm");
        timeBasedButton.setToggleGroup(group);
        dialogContent.getChildren().add(timeBasedButton);

        RadioButton batchItemButton = new RadioButton("Batch Item Algorithm");
        batchItemButton.setToggleGroup(group);
        dialogContent.getChildren().add(batchItemButton);

        RadioButton memberPriorityButton = new RadioButton("Member Priority Algorithm");
        memberPriorityButton.setToggleGroup(group);
        dialogContent.getChildren().add(memberPriorityButton);

        RadioButton itemWeightButton = new RadioButton("Item Weight Algorithm");
        itemWeightButton.setToggleGroup(group);
        dialogContent.getChildren().add(itemWeightButton);

        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (group.getSelectedToggle() != null) {
                    RadioButton selectedButton = (RadioButton) group.getSelectedToggle();
                    return selectedButton.getText();
                }
            }
            return null;
        });

        String result = dialog.showAndWait().orElse(null);
        if (result != null) {
            switch (result) {
                case "Time Based Algorithm" -> algorithm = new TimeBasedAlgorithm();
                case "Batch Item Algorithm" -> algorithm = new BatchItemAlgorithm();
                case "Member Priority Algorithm" -> algorithm = new MemberPriorityAlgorithm();
                case "Item Weight Algorithm" -> algorithm = new ItemWeightAlgorithm();
            }
            cafeQueue.setAlgorithm(algorithm);
            queueList.clear();
            cafeQueue.getQueue().forEach(order -> queueList.add(order.toString()));
            currentAlgorithmLabel.setText("Current Algorithm: " + result);
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void toggleTheme(Parent root) {
        if (isDarkMode) {
            root.getStyleClass().remove("dark-mode");
            menuBox.lookupAll(".text").forEach(node -> {
                if (node instanceof Labeled labeled) {
                    labeled.setStyle("-fx-text-fill: black;");
                }
            });
            menuBox.lookupAll(".check-box").forEach(node -> {
                if (node instanceof CheckBox checkBox) {
                    checkBox.setStyle("-fx-text-fill: black;");
                }
            });
        } else {
            root.getStyleClass().add("dark-mode");
            menuBox.lookupAll(".text").forEach(node -> {
                if (node instanceof Labeled labeled) {
                    labeled.setStyle("-fx-text-fill: white;");
                }
            });
            menuBox.lookupAll(".check-box").forEach(node -> {
                if (node instanceof CheckBox checkBox) {
                    checkBox.setStyle("-fx-text-fill: white;");
                }
            });
        }
        isDarkMode = !isDarkMode;
    }

    // Adds a button to clear processed orders
    private void addClearProcessedOrdersButton(HBox buttonBox) {
        Button clearProcessedOrdersButton = new Button("Clear Processed Orders");
        clearProcessedOrdersButton.setOnAction(e -> processedOrdersList.clear());
        buttonBox.getChildren().add(clearProcessedOrdersButton);
    }

    private void removeItemDialog(VBox menuBox) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Remove Item");

        VBox dialogContent = new VBox(10);
        ComboBox<String> itemComboBox = new ComboBox<>(menuItems);
        itemComboBox.setPromptText("Select Item to Remove");

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> {
            String selectedItem = itemComboBox.getValue();
            if (selectedItem == null) {
                showErrorDialog("Please select an item to remove.");
            } else {
                menuItems.remove(selectedItem);
                itemWeights.remove(selectedItem);

                menuBox.getChildren().removeIf(node -> {
                    if (node instanceof HBox itemBox) {
                        Label itemLabel = (Label) itemBox.getChildren().get(0);
                        return itemLabel.getText().equals(selectedItem);
                    }
                    return false;
                });
                dialog.close();
            }
        });

        dialogContent.getChildren().addAll(
                new Label("Select an item to remove:"),
                itemComboBox,
                removeButton
        );
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // Removes weight numbers in the left-up section
    private HBox createItemBox(String item, ListView<String> cartView) {
        HBox itemBox = new HBox(10);
        Label itemLabel = new Label(item);
        Button addButton = new Button("+");
        Button removeButton = new Button("-");

        addButton.setOnAction(e -> {
            int weight = itemWeights.getOrDefault(item, 1);
            currentOrder.put(item, currentOrder.getOrDefault(item, 0) + weight);
            updateCartView(cartView);
        });

        removeButton.setOnAction(e -> {
            int weight = itemWeights.getOrDefault(item, 1);
            if (currentOrder.getOrDefault(item, 0) >= weight) {
                currentOrder.put(item, currentOrder.get(item) - weight);
                if (currentOrder.get(item) == 0) currentOrder.remove(item);
                updateCartView(cartView);
            }
        });

        // Add only the item label, "+" and "-" buttons
        itemBox.getChildren().addAll(itemLabel, addButton, removeButton);
        return itemBox;
    }


    private void updateCartView(ListView<String> cartView) {
        cartView.getItems().clear();
        currentOrder.forEach((item, qty) -> {
            int weight = itemWeights.getOrDefault(item, 1);
            int quantity = qty / weight; // Convert back to the number of items
            cartView.getItems().add(item + " x" + quantity); // Exclude weight display
        });
    }

    private void showAddNewItemDialog(VBox menuBox, ListView<String> cartView, CheckBox memberCheckBox) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");

        VBox dialogContent = new VBox(10);
        TextField itemNameField = new TextField();
        itemNameField.setPromptText("Item Name");

        TextField weightField = new TextField();
        weightField.setPromptText("Item Weight");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String newItem = itemNameField.getText().trim();
            if (newItem.isEmpty()) {
                showErrorDialog("Item name cannot be empty.");
            } else if (menuItems.contains(newItem)) {
                showErrorDialog("This item already exists!");
            } else {
                try {
                    int weight = Integer.parseInt(weightField.getText());
                    if (weight <= 0) throw new NumberFormatException();
                    menuItems.add(newItem);
                    itemWeights.put(newItem, weight);

                    // Place new item above the member checkbox
                    int memberIndex = menuBox.getChildren().indexOf(memberCheckBox);
                    menuBox.getChildren().add(memberIndex, createItemBox(newItem, cartView));
                    dialog.close();
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid weight. Must be a positive integer.");
                }
            }
        });

        dialogContent.getChildren().addAll(
                new Label("Enter new item details:"),
                itemNameField,
                weightField,
                addButton
        );
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showChangeItemWeightDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Change Item Weight");

        VBox dialogContent = new VBox(10);
        ComboBox<String> itemComboBox = new ComboBox<>(menuItems);
        itemComboBox.setPromptText("Select Item");

        TextField weightField = new TextField();
        weightField.setPromptText("New Weight");

        itemComboBox.setOnAction(e -> {
            String selectedItem = itemComboBox.getValue();
            if (selectedItem != null) {
                int currentWeight = itemWeights.getOrDefault(selectedItem, 1);
                weightField.setText(String.valueOf(currentWeight));
            }
        });

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            String selectedItem = itemComboBox.getValue();
            if (selectedItem == null) {
                showErrorDialog("Please select an item.");
            } else {
                try {
                    int newWeight = Integer.parseInt(weightField.getText());
                    if (newWeight <= 0) throw new NumberFormatException();
                    itemWeights.put(selectedItem, newWeight);
                    dialog.close();
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid weight. Must be a positive integer.");
                }
            }
        });

        dialogContent.getChildren().addAll(
                new Label("Select an item and set its weight:"),
                itemComboBox,
                weightField,
                applyButton
        );
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showTimeValuesDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Time Values");

        VBox dialogContent = new VBox(10);
        Label currentValues = new Label("Current: Low = " + timeWeightedAlgorithm.getTimeThresholds()[0] +
                ", Mid = " + timeWeightedAlgorithm.getTimeThresholds()[1]);

        TextField lowField = new TextField(String.valueOf(timeWeightedAlgorithm.getTimeThresholds()[0]));
        TextField midField = new TextField(String.valueOf(timeWeightedAlgorithm.getTimeThresholds()[1]));

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            try {
                int low = Integer.parseInt(lowField.getText());
                int mid = Integer.parseInt(midField.getText());

                if (low >= mid) {
                    showErrorDialog("Low must be less than Mid!");
                } else {
                    timeWeightedAlgorithm.setTimeThresholds(low, mid);
                    dialog.close();
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter valid numeric values.");
            }
        });

        dialogContent.getChildren().addAll(currentValues, lowField, midField, applyButton);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
