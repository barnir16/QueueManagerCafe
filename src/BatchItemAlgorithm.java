import java.util.ArrayList;
import java.util.List;

/**
 * BatchItemAlgorithm - Implements priority calculation based on repetitions in an order.
 */
public class BatchItemAlgorithm implements IQueueAlgorithm {
    private final int baseWeight;
    private final int repetitionWeight;
    private final List<Order> queue = new ArrayList<>();

    public BatchItemAlgorithm(int baseWeight, int repetitionWeight) {
        this.baseWeight = baseWeight;
        this.repetitionWeight = repetitionWeight;
    }

    public BatchItemAlgorithm() {
        this(1, 1);
    }

    @Override
    public void addOrder(Order order) {
        queue.add(order);
    }

    @Override
    public int calculatePriority(Order order) {
        int priority = baseWeight;
        for (int quantity : order.getItems().values()) {
            if (quantity > 1) {
                priority += (quantity - 1) * repetitionWeight;
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
