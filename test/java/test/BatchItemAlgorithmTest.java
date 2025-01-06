package test;

import algorithm.BatchItemAlgorithm;
import model.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchItemAlgorithmTest {
    @Test
    public void testAddOrder() {
        BatchItemAlgorithm algorithm = new BatchItemAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Cappuccino", 2);
        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        assertEquals(1, algorithm.getQueue().size(), "Order should be added to the queue");
    }

    @Test
    public void testProcessNextOrder() {
        BatchItemAlgorithm algorithm = new BatchItemAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Espresso", 3);
        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(order, processedOrder, "Processed order should match the first order added");
    }
}
