package dev.javabybuilding.inventory;

/**
 * One product on the shelf. The fields are private: other classes cannot touch them directly. They must use the
 * methods below, and the methods refuse bad values. That protection is called encapsulation.
 * Money is whole centavos (int), as always.
 */
public class Product {

    /**
     * Largest unit price we accept: PHP 10,000.00. Keeps int centavo arithmetic safe.
     * static: ONE value shared by the whole class (not one per product). final: it can never change.
     * (It used to live in Menu as MAX_PRICE; the rule is about products, so it moved here.)
     */
    public static final int MAX_PRICE = 1_000_000;

    /**
     * TODO 1: static: needs no product to answer. True when the price is from 1 centavo up to MAX_PRICE (both allowed).
     * (Inventory.addProduct and setPrice already call it.)
     */
    public static boolean isValidPrice(int priceCentavos) {
        return false;
    }

    /** TODO 1: static: true when the reorder level is 0 or more. */
    public static boolean isValidReorderLevel(int reorderLevel) {
        return false;
    }

    private final String name;   // final: set once in the constructor, never changed (so there is no setter)
    private int priceCentavos;
    private int quantity;
    private int reorderLevel;

    /**
     * A new product starts with 0 in stock.
     * shortcut: a constructor cannot return false, so it trusts its caller (Inventory.addProduct checks first);
     * lesson 16 (exceptions) closes this gap.
     */
    public Product(String name, int priceCentavos, int reorderLevel) {
        this.name = name;
        this.priceCentavos = priceCentavos;
        this.quantity = 0;
        this.reorderLevel = reorderLevel;
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

    /** Changes the price. Returns false (and changes nothing) unless isValidPrice says the new price is allowed. */
    public boolean setPrice(int newPriceCentavos) {
        if (!isValidPrice(newPriceCentavos)) {
            return false;
        }
        priceCentavos = newPriceCentavos;
        return true;
    }

    /** Changes the reorder level. Returns false (and changes nothing) unless isValidReorderLevel allows it. */
    public boolean setReorderLevel(int newReorderLevel) {
        if (!isValidReorderLevel(newReorderLevel)) {
            return false;
        }
        reorderLevel = newReorderLevel;
        return true;
    }

    /** "OUT", "LOW" or "OK" (the same rule as lesson 03). */
    public String status() {
        return StockChecker.status(quantity, reorderLevel);
    }

    /** Value of this product's stock on the shelf, in centavos: unit price x quantity. */
    public int stockValue() {
        return PriceCalculator.lineTotal(priceCentavos, quantity);
    }

    /** Adds amount units to the shelf. Returns false (and changes nothing) when amount is 0 or less. */
    public boolean receive(int amount) {
        if (amount <= 0) {
            return false;
        }
        quantity += amount;
        return true;
    }

    /**
     * Takes amount units off the shelf and returns true. Returns false (and changes nothing) when amount is 0 or less
     * or there is not enough stock.
     */
    public boolean sell(int amount) {
        if (amount <= 0 || amount > quantity) {
            return false;
        }
        quantity -= amount;
        return true;
    }

    /** Text for printing, for example "Rice 25kg (12 in stock at PHP 1,250.50)". */
    @Override
    public String toString() {
        return name + " (" + quantity + " in stock at " + PriceCalculator.format(priceCentavos) + ")";
    }
}
