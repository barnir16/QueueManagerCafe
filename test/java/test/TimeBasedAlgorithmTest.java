package test;

import algorithm.TimeBasedAlgorithm;
import model.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeBasedAlgorithmTest {
    @Test
    public void testAddOrder() {
        TimeBasedAlgorithm algorithm = new TimeBasedAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Latte", 1);
        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        assertEquals(1, algorithm.getQueue().size(), "Order should be added to the queue");
    }

    @Test
    public void testProcessNextOrder() {
        TimeBasedAlgorithm algorithm = new TimeBasedAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Espresso", 1);

        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(order, processedOrder, "Processed order should match the first order added");
    }
}
