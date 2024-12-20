import java.util.Map;

/**
 * Represents a customer's order with a unique numeric ID and a list of items.
 */
public class Order {
    private final int id;
    private final Map<String, Integer> items;

    /**
     * Constructor to create an order.
     *
     * @param id    Unique identifier for the order.
     * @param items A map of item names to their respective quantities.
     */
    public Order(int id, Map<String, Integer> items) {
        this.id = id;
        this.items = items;
    }

    /**
     * Get the unique ID of the order.
     *
     * @return The order ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the items in the order.
     *
     * @return A map of item names to quantities.
     */
    public Map<String, Integer> getItems() {
        return items;
    }
}
