import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * WeightedPriorityAlgorithm - Implements priority calculation based on the total quantity of items in an order.
 */
public class WeightedPriorityAlgorithm implements IQueueAlgorithm {
    private final List<Order> queue = new ArrayList<>();

    @Override
    public void addOrder(Order order) {
        queue.add(order);
    }

    @Override
    public int calculatePriority(Order order) {
        // Priority is equal to the total number of items in the order.
        return order.getItems().values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public Order processNextOrder() {
        if (queue.isEmpty()) return null;

        // Process order based on priority
        Order highestPriorityOrder = queue.stream()
                .max(Comparator.comparingInt(this::calculatePriority))
                .orElse(null);

        queue.remove(highestPriorityOrder);
        return highestPriorityOrder;
    }

    @Override
    public List<Order> getQueue() {
        return new ArrayList<>(queue);
    }
}
