package test;

import algorithm.MemberPriorityAlgorithm;
import model.Order;
import queue.CafeOrderQueue;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CafeOrderQueueTest {
    @Test
    public void testAddOrder() {
        CafeOrderQueue queue = new CafeOrderQueue(new MemberPriorityAlgorithm());
        Map<String, Integer> items = new HashMap<>();
        items.put("Cappuccino", 1);
        Order order = new Order(1, items, true);

        queue.addOrder(order);

        assertEquals(1, queue.getQueue().size(), "Order should be added to the queue");
    }

    @Test
    public void testProcessNextOrder() {
        CafeOrderQueue queue = new CafeOrderQueue(new MemberPriorityAlgorithm());
        Map<String, Integer> items = new HashMap<>();
        items.put("Espresso", 1);
        Order order = new Order(1, items, true);

        queue.addOrder(order);

        Order processedOrder = queue.processNextOrder();
        assertEquals(order, processedOrder, "Processed order should match the first order added");
    }
}
