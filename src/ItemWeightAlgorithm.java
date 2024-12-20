import java.util.ArrayList;
import java.util.List;

/**
 * ItemWeightAlgorithm - Implements priority calculation based on specific weights assigned to items.
 */
public class ItemWeightAlgorithm implements IQueueAlgorithm {
    private final List<Order> queue = new ArrayList<>();

    @Override
    public void addOrder(Order order) {
        queue.add(order);
    }

    @Override
    public int calculatePriority(Order order) {
        // Assign weights to specific items (example weights for simplicity)
        int priority = 0;
        for (String item : order.getItems().keySet()) {
            int quantity = order.getItems().get(item);
            switch (item) {
                case "Cappuccino":
                    priority += quantity * 3; // Weight for Cappuccino
                    break;
                case "Espresso":
                    priority += quantity * 2; // Weight for Espresso
                    break;
                default:
                    priority += quantity; // Default weight
            }
        }
        return priority;
    }

    @Override
    public Order processNextOrder() {
        if (queue.isEmpty()) return null;

        // Process order based on priority
        Order highestPriorityOrder = queue.stream()
                .max((o1, o2) -> Integer.compare(calculatePriority(o1), calculatePriority(o2)))
                .orElse(null);

        queue.remove(highestPriorityOrder);
        return highestPriorityOrder;
    }

    @Override
    public List<Order> getQueue() {
        return new ArrayList<>(queue);
    }
}
