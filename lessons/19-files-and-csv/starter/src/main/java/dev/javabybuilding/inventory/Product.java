package dev.javabybuilding.inventory;

/**
 * One product on the shelf. The fields are private: other classes cannot touch them directly. They must use the
 * methods below, and the methods refuse bad values by throwing an exception (lesson 10 returned false instead).
 * Money is whole centavos (int), as always.
 */
public class Product {

    /**
     * Largest unit price we accept: PHP 10,000.00. Keeps int centavo arithmetic safe.
     * static: ONE value shared by the whole class (not one per product). final: it can never change.
     * (It used to live in Menu as MAX_PRICE; the rule is about products, so it moved here.)
     */
    public static final int MAX_PRICE = 1_000_000;

    /** static: needs no product to answer. A price is valid from 1 centavo up to MAX_PRICE. */
    public static boolean isValidPrice(int priceCentavos) {
        return priceCentavos >= 1 && priceCentavos <= MAX_PRICE;
    }

    /** static: a reorder level is valid when it is 0 or more. */
    public static boolean isValidReorderLevel(int reorderLevel) {
        return reorderLevel >= 0;
    }

    private final String name;   // final: set once in the constructor, never changed (so there is no setter)
    private int priceCentavos;
    private int quantity;
    private int reorderLevel;

    /**
     * A new product starts with 0 in stock. A constructor cannot return false, but it CAN throw: this closes the gap
     * lesson 10 left open, so a Product with a blank name, a bad price or a negative reorder level can never exist.
     *
     * @throws InvalidInputException when the name is blank, the price is not allowed or the reorder level is below 0
     */
    public Product(String name, int priceCentavos, int reorderLevel) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("Product name must not be blank.");
        }
        requireValidPrice(priceCentavos);
        requireValidReorderLevel(reorderLevel);
        this.name = name;
        this.priceCentavos = priceCentavos;
        this.quantity = 0;
        this.reorderLevel = reorderLevel;
    }

    /**
     * Rebuilds a product that already has stock, for example when it is read back from a file. A new product on the
     * shelf still starts at 0: use the three-argument constructor and receive().
     *
     * @throws InvalidInputException for the same reasons as the other constructor, or when quantity is below 0
     */
    public Product(String name, int priceCentavos, int reorderLevel, int quantity) {
        this(name, priceCentavos, reorderLevel);
        if (quantity < 0) {
            throw new InvalidInputException("Quantity must not be negative.");
        }
        this.quantity = quantity;
    }

    // Getters: read-only access to the private fields.
    public String getName() {
        return name;
    }

    public int getPriceCentavos() {
        return priceCentavos;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    private static void requireValidPrice(int priceCentavos) {
        if (!isValidPrice(priceCentavos)) {
            throw new InvalidInputException("Price must be from PHP 0.01 to " + PriceCalculator.format(MAX_PRICE) + ".");
        }
    }

    private static void requireValidReorderLevel(int reorderLevel) {
        if (!isValidReorderLevel(reorderLevel)) {
            throw new InvalidInputException("Reorder level must be 0 or more.");
        }
    }

    /**
     * Changes the price. Nothing changes if it throws.
     *
     * @throws InvalidInputException unless isValidPrice says the new price is allowed
     */
    public void setPrice(int newPriceCentavos) {
        requireValidPrice(newPriceCentavos);
        priceCentavos = newPriceCentavos;
    }

    /**
     * Changes the reorder level. Nothing changes if it throws.
     *
     * @throws InvalidInputException when it is below 0
     */
    public void setReorderLevel(int newReorderLevel) {
        requireValidReorderLevel(newReorderLevel);
        reorderLevel = newReorderLevel;
    }

    /** "OUT", "LOW" or "OK" (the same rule as lesson 03). */
    public String status() {
        return StockChecker.status(quantity, reorderLevel);
    }

    /** Value of this product's stock on the shelf, in centavos: unit price x quantity. */
    public int stockValue() {
        return PriceCalculator.lineTotal(priceCentavos, quantity);
    }

    /**
     * Adds amount units to the shelf.
     *
     * @throws InvalidInputException when amount is 0 or less (nothing changes)
     */
    public void receive(int amount) {
        if (amount <= 0) {
            throw new InvalidInputException("Quantity must be at least 1.");
        }
        quantity += amount;
    }

    /**
     * Takes amount units off the shelf.
     *
     * @throws InvalidInputException when amount is 0 or less
     * @throws InsufficientStockException when there is not enough on the shelf (nothing changes)
     */
    public void sell(int amount) {
        if (amount <= 0) {
            throw new InvalidInputException("Quantity must be at least 1.");
        }
        if (amount > quantity) {
            throw new InsufficientStockException(name, amount, quantity);
        }
        quantity -= amount;
    }

    /** Text for printing, for example "Rice 25kg (12 in stock at PHP 1,250.50)". */
    @Override
    public String toString() {
        return name + " (" + quantity + " in stock at " + PriceCalculator.format(priceCentavos) + ")";
    }
}
