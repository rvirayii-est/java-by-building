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

    /** A new product starts with 0 in stock. */
    public Product(String name, int priceCentavos, int reorderLevel) {
        this.name = name;
        this.priceCentavos = priceCentavos;
        this.quantity = 0;
        this.reorderLevel = reorderLevel;
    }

    /** "OUT", "LOW" or "OK" (the same rule as lesson 03). */
    public String status() {
        return StockChecker.status(quantity, reorderLevel);
    }

    /** Value of this product's stock on the shelf, in centavos: unit price x quantity. */
    public int stockValue() {
        return PriceCalculator.lineTotal(priceCentavos, quantity);
    }

    /** Adds amount units to the shelf. */
    public void receive(int amount) {
        quantity += amount;
    }

    /** Takes amount units off the shelf and returns true. If there is not enough, changes nothing and returns false. */
    public boolean sell(int amount) {
        if (amount > quantity) {
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
