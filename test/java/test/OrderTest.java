package test;

import model.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {
    @Test
    public void testOrderCreation() {
        Map<String, Integer> items = new HashMap<>();
        items.put("Americano", 1);
        Order order = new Order(1, items, true);

        assertEquals(1, order.getOrderId());
        assertEquals(items, order.getItems());
        assertEquals(true, order.isMember());
    }

    @Test
    public void testOrderTimestamp() {
        Map<String, Integer> items = new HashMap<>();
        items.put("Latte", 1);
        Order order = new Order(2, items, false);

        assertEquals(false, order.isMember());
    }
}
