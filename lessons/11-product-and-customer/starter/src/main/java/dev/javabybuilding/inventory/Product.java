package dev.javabybuilding.inventory;

/**
 * One product on the shelf. The fields are private: other classes cannot touch them directly. They must use the
 * methods below, and the methods refuse bad values. That protection is called encapsulation.
 * Money is whole centavos (int), as always.
 */
public class Product {

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

    /** Changes the price. Returns false (and changes nothing) unless the new price is above 0. */
    public boolean setPrice(int newPriceCentavos) {
        if (newPriceCentavos <= 0) {
            return false;
        }
        priceCentavos = newPriceCentavos;
        return true;
    }

    /** Changes the reorder level. Returns false (and changes nothing) when it is below 0. */
    public boolean setReorderLevel(int newReorderLevel) {
        if (newReorderLevel < 0) {
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
