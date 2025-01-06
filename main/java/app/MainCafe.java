package app;

import algorithm.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Order;
import queue.CafeOrderQueue;
import queue.IQueueAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class MainCafe extends Application {
    private static int currentOrderId = 1;
    private boolean isDarkMode = false;

    private final ObservableList<String> queueList = FXCollections.observableArrayList();
    private final ObservableList<String> processedOrdersList = FXCollections.observableArrayList();
    private final Map<String, Integer> currentOrder = new HashMap<>();
    private final String[] menuItems = {"Cappuccino", "Espresso", "Sandwich", "Muffin"};

    private IQueueAlgorithm algorithm = new TimeBasedAlgorithm();
    private CafeOrderQueue cafeQueue = new CafeOrderQueue(algorithm);
    private Label currentAlgorithmLabel;
    private VBox mainLayout;
    private VBox menuBox;
    private final TimeWeightedAlgorithm timeAlgorithm = new TimeWeightedAlgorithm();

    @Override
    public void start(Stage primaryStage) {
        ListView<String> cartView = createStyledListView("Your cart is empty");
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
            HBox itemBox = new HBox(10);
            Label itemLabel = new Label(item);
            Button addButton = new Button("+");
            Button removeButton = new Button("-");
            Label quantityLabel = new Label("0");

            addButton.setOnAction(e -> {
                currentOrder.put(item, currentOrder.getOrDefault(item, 0) + 1);
                quantityLabel.setText(String.valueOf(currentOrder.get(item)));
                updateCartView(cartView);
            });

            removeButton.setOnAction(e -> {
                if (currentOrder.getOrDefault(item, 0) > 0) {
                    currentOrder.put(item, currentOrder.get(item) - 1);
                    if (currentOrder.get(item) == 0) currentOrder.remove(item);
                    quantityLabel.setText(String.valueOf(currentOrder.getOrDefault(item, 0)));
                    updateCartView(cartView);
                }
            });

            itemBox.getChildren().addAll(itemLabel, addButton, removeButton, quantityLabel);
            menuBox.getChildren().add(itemBox);
        }

        menuBox.getChildren().add(new CheckBox("Member"));
        return menuBox;
    }

    private HBox createButtonBox(ListView<String> cartView, ListView<String> queueListView) {
        HBox buttonBox = new HBox(10);
        Button placeOrderButton = new Button("Place Order");
        Button processOrderButton = new Button("Process Order");
        Button toggleThemeButton = new Button("Toggle Theme");
        Button setTimeValuesButton = new Button("Set Time Values");
        Button setTimeWeightsButton = new Button("Set Time Weights");
        Button switchAlgorithmButton = new Button("Switch Algorithm");

        placeOrderButton.setOnAction(e -> {
            if (!currentOrder.isEmpty()) {
                int orderId = currentOrderId++;
                CheckBox memberCheckBox = (CheckBox) menuBox.getChildren().get(menuItems.length);
                boolean isMember = memberCheckBox.isSelected();
                cafeQueue.addOrder(new Order(orderId, new HashMap<>(currentOrder), isMember));
                queueList.clear();
                cafeQueue.getQueue().forEach(order -> queueList.add(order.toString()));
                currentOrder.clear();
                cartView.getItems().clear();
            }
        });

        processOrderButton.setOnAction(e -> {
            Order processedOrder = cafeQueue.processNextOrder();
            if (processedOrder != null) {
                queueList.clear();
                cafeQueue.getQueue().forEach(order -> queueList.add(order.toString()));
                processedOrdersList.add(processedOrder.toString());
            }
        });

        toggleThemeButton.setOnAction(e -> toggleTheme(mainLayout));
        setTimeValuesButton.setOnAction(e -> showTimeValuesDialog());
        setTimeWeightsButton.setOnAction(e -> showTimeWeightsDialog());
        switchAlgorithmButton.setOnAction(e -> showAlgorithmSelectionDialog());

        buttonBox.getChildren().addAll(
                placeOrderButton,
                processOrderButton,
                toggleThemeButton,
                setTimeValuesButton,
                setTimeWeightsButton,
                switchAlgorithmButton
        );
        return buttonBox;
    }

    private void showAlgorithmSelectionDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Algorithm");

        VBox dialogContent = new VBox(10);
        ToggleGroup group = new ToggleGroup();

        if (!(algorithm instanceof TimeBasedAlgorithm)) {
            RadioButton timeBasedButton = new RadioButton("Time Based Algorithm");
            timeBasedButton.setToggleGroup(group);
            dialogContent.getChildren().add(timeBasedButton);
        }

        if (!(algorithm instanceof BatchItemAlgorithm)) {
            RadioButton batchItemButton = new RadioButton("Batch Item Algorithm");
            batchItemButton.setToggleGroup(group);
            dialogContent.getChildren().add(batchItemButton);
        }

        if (!(algorithm instanceof MemberPriorityAlgorithm)) {
            RadioButton memberPriorityButton = new RadioButton("Member Priority Algorithm");
            memberPriorityButton.setToggleGroup(group);
            dialogContent.getChildren().add(memberPriorityButton);
        }

        if (!(algorithm instanceof ItemWeightAlgorithm)) {
            RadioButton itemWeightButton = new RadioButton("Item Weight Algorithm");
            itemWeightButton.setToggleGroup(group);
            dialogContent.getChildren().add(itemWeightButton);
        }

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
                case "Time Based Algorithm" -> {
                    algorithm = new TimeBasedAlgorithm();
                    currentAlgorithmLabel.setText("Current Algorithm: Time Based Algorithm");
                }
                case "Batch Item Algorithm" -> {
                    algorithm = new BatchItemAlgorithm();
                    currentAlgorithmLabel.setText("Current Algorithm: Batch Item Algorithm");
                }
                case "Member Priority Algorithm" -> {
                    algorithm = new MemberPriorityAlgorithm();
                    currentAlgorithmLabel.setText("Current Algorithm: Member Priority Algorithm");
                }
                case "Item Weight Algorithm" -> {
                    algorithm = new ItemWeightAlgorithm();
                    currentAlgorithmLabel.setText("Current Algorithm: Item Weight Algorithm");
                }
            }
            cafeQueue.setAlgorithm(algorithm);

            queueList.clear();
            cafeQueue.getQueue().forEach(order -> queueList.add(order.toString()));
        }
    }

    private void showTimeValuesDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Time Values");

        VBox dialogContent = new VBox(10);
        Label currentValues = new Label("Current Values: Low = " + timeAlgorithm.getTimeThresholds()[0]
                + ", Mid = " + timeAlgorithm.getTimeThresholds()[1]
                + ", High = " + timeAlgorithm.getTimeThresholds()[2]);
        TextField lowField = new TextField();
        TextField midField = new TextField();
        TextField highField = new TextField();

        lowField.setPromptText("Enter Low Time (minutes)");
        midField.setPromptText("Enter Mid Time (minutes)");
        highField.setPromptText("Enter High Time (minutes)");

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            try {
                int low = Integer.parseInt(lowField.getText());
                int mid = Integer.parseInt(midField.getText());
                int high = Integer.parseInt(highField.getText());

                if (low > mid || mid > high) {
                    showErrorDialog("Invalid input. Ensure Low ≤ Mid ≤ High.");
                    return;
                }

                timeAlgorithm.setTimeThresholds(low, mid, high);
                currentValues.setText("Updated Values: Low = " + low
                        + ", Mid = " + mid
                        + ", High = " + high);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid input. Please enter numeric values.");
            }
        });

        dialogContent.getChildren().addAll(currentValues, lowField, midField, highField, applyButton);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showTimeWeightsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Time Weights");

        VBox dialogContent = new VBox(10);
        Label currentValues = new Label("Current Values: Low = " + timeAlgorithm.getWeights()[0]
                + ", Mid = " + timeAlgorithm.getWeights()[1]
                + ", High = " + timeAlgorithm.getWeights()[2]);
        TextField lowField = new TextField();
        TextField midField = new TextField();
        TextField highField = new TextField();

        lowField.setPromptText("Enter Low Weight");
        midField.setPromptText("Enter Mid Weight");
        highField.setPromptText("Enter High Weight");

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            try {
                int low = Integer.parseInt(lowField.getText());
                int mid = Integer.parseInt(midField.getText());
                int high = Integer.parseInt(highField.getText());

                if (low <= 0 || mid <= 0 || high <= 0) {
                    showErrorDialog("Invalid input. Ensure all weights are positive.");
                    return;
                }

                timeAlgorithm.setWeights(low, mid, high);
                currentValues.setText("Updated Values: Low = " + low
                        + ", Mid = " + mid
                        + ", High = " + high);
            } catch (NumberFormatException ex) {
                showErrorDialog("Invalid input. Please enter numeric values.");
            }
        });

        dialogContent.getChildren().addAll(currentValues, lowField, midField, highField, applyButton);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void toggleTheme(Parent root) {
        if (isDarkMode) {
            root.getStyleClass().remove("dark-mode");
        } else {
            root.getStyleClass().add("dark-mode");
        }
        isDarkMode = !isDarkMode;
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateCartView(ListView<String> cartView) {
        cartView.getItems().clear();
        currentOrder.forEach((item, qty) -> cartView.getItems().add(item + " x" + qty));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
