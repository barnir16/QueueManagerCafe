# Cafe Order Management Application with Algorithm Library

## Overview

This project consists of a **Cafe Order Management GUI Application** and a **JAR library containing queue management algorithms**. The application demonstrates the use of modular algorithms for prioritizing orders in a dynamic, flexible way. The JAR library is designed as a **general-purpose library** that can be reused to create new algorithms and applied to various use cases beyond the scope of this project.

---

## JAR Library Documentation

### Included Algorithms
1. **Time-Based Algorithm**
    - **Description**: Prioritizes orders based on the time they were placed. Orders with the earliest placement time are processed first.
    - **Calculation**: No additional weights are applied. Orders are processed in FIFO (First-In-First-Out) order based on timestamps.
    - **Example**:
        - Order 1 placed at 12:00:00.
        - Order 2 placed at 12:01:00.
        - **Processing order**: Order 1 -> Order 2.

2. **Time-Weighted Algorithm**
    - **Description**: Assigns weights to orders based on how long they have been waiting, divided into "Low", "Medium", and "High" time thresholds.
    - **Calculation**:
        - Default weights: Low = 1, Medium = 2, High = 3.
        - Orders are processed with higher weight taking priority.
    - **Example**:
        - Order 1 (waiting time = 10 seconds): Weight = 1 (Low).
        - Order 2 (waiting time = 90 seconds): Weight = 3 (High).
        - **Processing order**: Order 2 -> Order 1.

3. **Item Weight Algorithm**
    - **Description**: Adjusts order priorities based on item weights in addition to time weights.
    - **Calculation**:
        - Each item has a default weight (e.g., Espresso = 1, Cappuccino = 2).
        - Total weight = (Item Weight × Quantity) + Time Weight.
    - **Example**:
        - Order 1: Espresso (1x) = 1 + Time Weight = 2.
        - Order 2: Cappuccino (1x) = 2 + Time Weight = 3.
        - **Processing order**: Order 2 -> Order 1.

4. **Member Priority Algorithm**
    - **Description**: Adds priority to orders placed by members while still considering time weights.
    - **Calculation**:
        - Member orders receive an additional weight (default = +2).
        - Total weight = Time Weight + Member Weight.
    - **Example**:
        - Order 1 (Non-Member): Time Weight = 2.
        - Order 2 (Member): Time Weight = 2 + Member Weight (2) = 4.
        - **Processing order**: Order 2 -> Order 1.

5. **Batch Item Algorithm**
    - **Description**: Groups items in batches for efficiency, adding a batch weight to time-weighted calculations.
    - **Calculation**:
        - Batch weight = Number of identical items × Batch Factor.
        - Total weight = Batch Weight + Time Weight.
    - **Example**:
        - Order 1: 3 Espressos (Batch Weight = 3 × 1) + Time Weight = 4.
        - Order 2: 2 Muffins (Batch Weight = 2 × 1) + Time Weight = 3.
        - **Processing order**: Order 1 -> Order 2.

---

## GUI Application Features

### User Interface
- **Menu Box**:
    - Add, remove, and update item weights dynamically.
    - Select "Member" for additional priority.
- **Cart View**:
    - Displays current items in the cart.
    - Automatically calculates weights based on algorithms.
- **Queue View**:
    - Shows current orders in the queue.
    - Orders are automatically updated based on the selected algorithm.
- **Processed Orders**:
    - Displays processed orders with timestamps and priority details.
    - Includes a "Clear Processed Orders" button.

### Supported Algorithms
1. **Time-Based Algorithm**: Default processing order.
2. **Time-Weighted Algorithm**: Prioritizes orders based on waiting time.
3. **Item Weight Algorithm**: Incorporates item-specific weights.
4. **Member Priority Algorithm**: Adds weight for member orders.
5. **Batch Item Algorithm**: Groups identical items for priority.

### Buttons and Controls
1. **Place Order**: Adds the current cart to the queue.
2. **Process Order**: Processes the next order in the queue.
3. **Clear Processed Orders**: Clears the processed orders list.
4. **Toggle Theme**: Switches between light and dark mode.
5. **Set Time Values**: Adjusts thresholds for time weights.
6. **Set Time Weights**: Updates weights for time categories.
7. **Switch Algorithm**: Allows dynamic switching of algorithms.

---

## Installation and Setup

1. Clone this repository.
2. Add the `IQueueHandleAlgorithm.jar` library to your project.
3. Run the `MainCafe` class from an IDE (e.g., IntelliJ) or command line.
4. Ensure JavaFX is configured correctly in your environment.

---

## Example Use Cases
1. **Cafe Order Management**:
    - Prioritize orders based on membership, waiting time, and batch efficiency.
2. **Restaurant Seating**:
    - Use algorithms to manage reservations based on customer type and reservation time.
3. **Delivery Scheduling**:
    - Assign delivery priority based on package weight and delivery time.

---

## Contributing

This project is open for contributions. Please create a pull request for any changes or enhancements.

---

## License

This project is licensed under the MIT License.

## Project Team
This library and application were created by:

Bar Muller Nir (208463026)
Aviv Malul (208667089)
Project developed as part of a coursework requirement for HIT (Holon Institute of Technology).

