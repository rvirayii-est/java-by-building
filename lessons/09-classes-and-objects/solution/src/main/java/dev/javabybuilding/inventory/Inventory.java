package dev.javabybuilding.inventory;

import java.util.ArrayList;

/**
 * The shop's stock: one ArrayList of Product objects. (Lessons 07-08 used an ArrayList of names plus three HashMaps;
 * now each Product carries its own price, quantity and reorder level.) The methods below behave exactly as before.
 * Money is whole centavos (int), as always.
 */
public class Inventory {

    private final ArrayList<Product> products = new ArrayList<>();
    private int salesTotal = 0; // shortcut: an int of centavos overflows near PHP 21 million, fine until week 5 (BigDecimal)

    /** The four products from the stock report, with the same numbers as lessons 03-08. */
    public static Inventory sample() {
        Inventory inventory = new Inventory();
        inventory.addProduct("Rice 25kg", 125050, 10);
        inventory.addProduct("Cooking Oil 1L", 9800, 10);
        inventory.addProduct("Sugar 1kg", 7500, 10);
        inventory.addProduct("Sardines 155g", 2400, 20);
        inventory.receive("Rice 25kg", 12);
        inventory.receive("Cooking Oil 1L", 4);
        inventory.receive("Sardines 155g", 60);
        return inventory;
    }

    /** The Product object whose name matches (ignoring capitals and spaces), or null when there is none. */
    private Product findProduct(String name) {
        for (Product product : products) {
            if (TextUtil.sameName(product.name, name)) {
                return product;
            }
        }
        return null;
    }

    /** The name as stored (for example "Sugar 1kg") when name matches a product ignoring capitals and spaces, else null. */
    public String find(String name) {
        Product product = findProduct(name);
        if (product == null) {
            return null;
        }
        return product.name;
    }

    /**
     * Adds a new product with 0 in stock. Returns false (and changes nothing) when the name is blank, the product
     * already exists, the price is not above 0, or the reorder level is below 0.
     */
    public boolean addProduct(String name, int priceCentavos, int reorderLevel) {
        String clean = TextUtil.clean(name);
        if (clean.isEmpty() || findProduct(clean) != null || priceCentavos <= 0 || reorderLevel < 0) {
            return false;
        }
        products.add(new Product(clean, priceCentavos, reorderLevel));
        return true;
    }

    /** Units in stock, or -1 when there is no such product. */
    public int quantityOf(String name) {
        Product product = findProduct(name);
        if (product == null) {
            return -1;
        }
        return product.quantity;
    }

    /** Unit price in centavos, or -1 when there is no such product. */
    public int priceOf(String name) {
        Product product = findProduct(name);
        if (product == null) {
            return -1;
        }
        return product.priceCentavos;
    }

    /** Adds amount units. Returns false (and changes nothing) for an unknown product or an amount of 0 or less. */
    public boolean receive(String name, int amount) {
        Product product = findProduct(name);
        if (product == null || amount <= 0) {
            return false;
        }
        product.receive(amount);
        return true;
    }

    /**
     * Sells amount units. Returns the sale total in centavos (unit price x amount), or -1 and changes nothing when the
     * product is unknown, the amount is 0 or less, or there is not enough stock.
     */
    public int sell(String name, int amount) {
        Product product = findProduct(name);
        if (product == null || amount <= 0 || !product.sell(amount)) {
            return -1;
        }
        int total = PriceCalculator.lineTotal(product.priceCentavos, amount);
        salesTotal += total;
        return total;
    }

    public int size() {
        return products.size();
    }

    /** Everything sold so far, in centavos. */
    public int salesTotal() {
        return salesTotal;
    }

    /** The stock report text (same format as lesson 03). */
    public String report() {
        return StockReport.build(products);
    }

    /** Value of everything on the shelves, in centavos. */
    public int totalValue() {
        return StockReport.totalValue(products);
    }
}
