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
     * A new product starts with 0 in stock. (Done for you.)
     * shortcut: a constructor cannot return false, so it trusts its caller (Inventory.addProduct checks first);
     * lesson 16 (exceptions) closes this gap.
     */
    public Product(String name, int priceCentavos, int reorderLevel) {
        this.name = name;
        this.priceCentavos = priceCentavos;
        this.quantity = 0;
        this.reorderLevel = reorderLevel;
    }

    // TODO 1: getters. Each one just returns its private field.
    public String getName() {
        return null;
    }

    public int getPriceCentavos() {
        return 0;
    }

    public int getQuantity() {
        return 0;
    }

    public int getReorderLevel() {
        return 0;
    }

    /** TODO 2: change the price and return true, but only when the new price is above 0. Otherwise change nothing and return false. */
    public boolean setPrice(int newPriceCentavos) {
        return false;
    }

    /** TODO 2: change the reorder level and return true, but only when it is 0 or more. Otherwise change nothing and return false. */
    public boolean setReorderLevel(int newReorderLevel) {
        return false;
    }

    /** "OUT", "LOW" or "OK" (the same rule as lesson 03). (Done for you.) */
    public String status() {
        return StockChecker.status(quantity, reorderLevel);
    }

    /** Value of this product's stock on the shelf, in centavos: unit price x quantity. (Done for you.) */
    public int stockValue() {
        return PriceCalculator.lineTotal(priceCentavos, quantity);
    }

    /**
     * TODO 3: add amount units to the shelf and return true. Return false (and change nothing) when amount is 0 or less.
     * (In lesson 09 this was void and trusted the caller. Now the Product protects itself.)
     */
    public boolean receive(int amount) {
        return false;
    }

    /**
     * TODO 3: take amount units off the shelf and return true. Return false (and change nothing) when amount is 0 or
     * less or there is not enough stock.
     */
    public boolean sell(int amount) {
        return false;
    }

    /** Text for printing, for example "Rice 25kg (12 in stock at PHP 1,250.50)". (Done for you.) */
    @Override
    public String toString() {
        return name + " (" + quantity + " in stock at " + PriceCalculator.format(priceCentavos) + ")";
    }
}
