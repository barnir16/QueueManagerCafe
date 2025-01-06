package test;

import algorithm.ItemWeightAlgorithm;
import model.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemWeightAlgorithmTest {
    @Test
    public void testAddOrder() {
        ItemWeightAlgorithm algorithm = new ItemWeightAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Espresso", 2);
        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        assertEquals(1, algorithm.getQueue().size(), "Order should be added to the queue");
    }

    @Test
    public void testProcessNextOrder() {
        ItemWeightAlgorithm algorithm = new ItemWeightAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Latte", 1);

        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(order, processedOrder, "Processed order should match the first order added");
    }
}
