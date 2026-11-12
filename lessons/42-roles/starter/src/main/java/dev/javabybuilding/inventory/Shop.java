package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Everything the menu needs from "the shop", whatever keeps the data. Two classes implement it: {@link Inventory}
 * (everything in memory, saved to CSV files by DataStore) and {@link ShopService} (everything in repositories, for
 * example a database). Menu and Main depend on this interface, so the console app runs on either one.
 */
public interface Shop {

    /** The name as stored when it matches a product ignoring capitals and spaces, else null. */
    String find(String name);

    /** @throws UnknownProductException when there is no such product */
    Product getProduct(String name);

    /** @throws InvalidInputException when the name is blank or taken, the price is not allowed, or the reorder level is below 0 */
    void addProduct(String name, BigDecimal price, int reorderLevel);

    /** @throws UnknownProductException when there is no such product */
    int quantityOf(String name);

    /** @throws UnknownProductException when there is no such product; InvalidInputException when amount is 0 or less */
    void receive(String name, int amount);

    /**
     * Sells amount units. If it throws, NOTHING changed.
     *
     * @throws UnknownProductException, InvalidInputException, InsufficientStockException as Inventory.sell documents
     */
    Sale sell(String name, int amount, Customer customer, Discount discount, PaymentMethod payment);

    /** A cash sale to the anonymous walk-in customer at full price; returns the total in pesos. */
    default BigDecimal sell(String name, int amount) {
        return sell(name, amount, Customer.WALK_IN, new NoDiscount(), PaymentMethod.CASH).total();
    }

    List<Product> products();

    List<Sale> sales();

    List<StockMovement> movements();

    /** The stock report text (lesson 03 layout). */
    String report();

    BigDecimal totalValue();

    /** Everything sold so far, in pesos. */
    BigDecimal salesTotal();

    List<ReorderSuggestion> reorderSuggestions();

    /** The health check of lesson 22: an empty list means the shelves and the stock log agree. */
    List<String> audit();
}
