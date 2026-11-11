package dev.javabybuilding.inventory;

/**
 * One product on the shelf. A class is a blueprint; each "new Product(...)" makes one object from it.
 * Every object keeps its own name, price, quantity and reorder level (its "fields").
 * Money is whole centavos (int), as always.
 */
public class Product {

    // Fields: the data each Product remembers. (Lesson 10 makes these private.)
    public String name;
    public int priceCentavos;
    public int quantity;
    public int reorderLevel;

    /** A new product starts with 0 in stock. (Done for you.) */
    public Product(String name, int priceCentavos, int reorderLevel) {
        this.name = name;
        this.priceCentavos = priceCentavos;
        this.quantity = 0;
        this.reorderLevel = reorderLevel;
    }

    /** TODO 1: return "OUT", "LOW" or "OK". You already have StockChecker.status(quantity, reorderLevel). */
    public String status() {
        return null;
    }

    /** TODO 1: value of this product's stock in centavos: PriceCalculator.lineTotal(priceCentavos, quantity). */
    public int stockValue() {
        return 0;
    }

    /** TODO 2: add amount units to the shelf (change the quantity field). */
    public void receive(int amount) {
    }

    /**
     * TODO 2: if amount is more than quantity, change nothing and return false. Otherwise take amount off
     * quantity and return true.
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
