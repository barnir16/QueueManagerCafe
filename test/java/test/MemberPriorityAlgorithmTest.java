package test;

import algorithm.MemberPriorityAlgorithm;
import model.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberPriorityAlgorithmTest {
    @Test
    public void testPriorityOrder() {
        MemberPriorityAlgorithm algorithm = new MemberPriorityAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Cappuccino", 1);
        Order memberOrder = new Order(1, items, true); // Member
        Order nonMemberOrder = new Order(2, items, false); // Non-member

        algorithm.addOrder(memberOrder);
        algorithm.addOrder(nonMemberOrder);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(memberOrder, processedOrder, "Member order should be prioritized over non-member order");
    }

    @Test
    public void testNonMemberOrder() {
        MemberPriorityAlgorithm algorithm = new MemberPriorityAlgorithm();
        Map<String, Integer> items = new HashMap<>();
        items.put("Espresso", 1);
        Order order = new Order(1, items, false);

        algorithm.addOrder(order);

        Order processedOrder = algorithm.processNextOrder();
        assertEquals(order, processedOrder, "Non-member order should be processed if no members exist");
    }
}
