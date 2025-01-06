package test;

import algorithm.TimeWeightedAlgorithm;
import model.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TimeWeightedAlgorithmTest {

    @Test
    public void testAddOrder() {
        TimeWeightedAlgorithm algorithm = new TimeWeightedAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Latte", 1);
        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        assertEquals(1, algorithm.getQueue().size(), "Order should be added to the queue");
    }

    @Test
    public void testProcessNextOrder() {
        TimeWeightedAlgorithm algorithm = new TimeWeightedAlgorithm();
        Map<String, Integer> items1 = new HashMap<>();
        items1.put("Espresso", 1);
        Order order1 = new Order(1, items1, false);

        Map<String, Integer> items2 = new HashMap<>();
        items2.put("Latte", 1);
        Order order2 = new Order(2, items2, false);

        algorithm.addOrder(order1);
        algorithm.addOrder(order2);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(order1, processedOrder, "Orders should be processed in FIFO order when weights are equal");
    }

    @Test
    public void testDynamicTimeThresholds() {
        TimeWeightedAlgorithm algorithm = new TimeWeightedAlgorithm();
        algorithm.setTimeThresholds(3, 7, 12);

        int[] thresholds = algorithm.getTimeThresholds();
        assertArrayEquals(new int[]{3, 7, 12}, thresholds, "Time thresholds should update dynamically");
    }

    @Test
    public void testDynamicWeights() {
        TimeWeightedAlgorithm algorithm = new TimeWeightedAlgorithm();
        algorithm.setWeights(2, 4, 6);

        int[] weights = algorithm.getWeights();
        assertArrayEquals(new int[]{2, 4, 6}, weights, "Weights should update dynamically");
    }

    @Test
    public void testPriorityBasedOnWaitingTime() throws InterruptedException {
        TimeWeightedAlgorithm algorithm = new TimeWeightedAlgorithm();
        Map<String, Integer> items1 = new HashMap<>();
        items1.put("Espresso", 1);
        Order order1 = new Order(1, items1, false);

        Map<String, Integer> items2 = new HashMap<>();
        items2.put("Latte", 1);
        Order order2 = new Order(2, items2, false);

        algorithm.addOrder(order1);
        Thread.sleep(2000); // Simulate waiting time
        algorithm.addOrder(order2);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(order2, processedOrder, "Order with longer waiting time should have higher priority");
    }

    @Test
    public void testInvalidThresholds() {
        TimeWeightedAlgorithm algorithm = new TimeWeightedAlgorithm();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            algorithm.setTimeThresholds(10, 5, 20);
        });

        assertTrue(exception.getMessage().contains("Ensure low < mid < high"), "Invalid thresholds should throw an exception");
    }
}
