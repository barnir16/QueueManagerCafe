package app.client.utils;

import model.Order;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for logging orders.
 */
public class OrderLogger {
    private static final Logger logger = Logger.getLogger(OrderLogger.class.getName());
    private static final String FILE_PATH = "orders_log.txt";

    public static void logOrders(List<Order> allOrders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Write Non-Processed Orders
            writer.write("--- Non-Processed Orders ---\n");
            for (Order order : allOrders) {
                if (order.getTimeProcessed() == null) { // Check if not processed
                    writer.write(String.format(
                            "Order ID: %d | Items: %s | Member: %s | Time Placed: %s\n",
                            order.getOrderId(),
                            order.getItems().toString(),
                            order.isMember() ? "Yes" : "No",
                            formatTime(order.getTimePlaced(), formatter)
                    ));
                }
            }
            writer.write("\n");

            // Write Processed Orders
            writer.write("--- Processed Orders ---\n");
            for (Order order : allOrders) {
                if (order.getTimeProcessed() != null) { // Check if processed
                    writer.write(String.format(
                            "Order ID: %d | Items: %s | Member: %s | Time Placed: %s | Time Processed: %s\n",
                            order.getOrderId(),
                            order.getItems().toString(),
                            order.isMember() ? "Yes" : "No",
                            formatTime(order.getTimePlaced(), formatter),
                            formatTime(order.getTimeProcessed(), formatter)
                    ));
                }
            }

            logger.info("Orders logged successfully to " + FILE_PATH);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to log file", e);
        }
    }

    private static String formatTime(java.time.LocalDateTime time, DateTimeFormatter formatter) {
        return (time == null) ? "N/A" : time.format(formatter);
    }
}
