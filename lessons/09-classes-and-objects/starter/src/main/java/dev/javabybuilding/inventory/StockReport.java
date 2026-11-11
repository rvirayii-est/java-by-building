package dev.javabybuilding.inventory;

import java.util.List;

public class StockReport {

    /**
     * TODO 3: one header line, then one line per product, in the order of the list. Same format as before:
     * String.format("%-14s%5s  %s", "Product", "Qty", "Status") for the header, and for each product
     * String.format("%-14s%5d  %s", product.name, product.quantity, product.status()) on its own line.
     * (Lesson 08 did this with three parallel arrays; now you loop over Product objects.)
     */
    public static String build(List<Product> products) {
        return null;
    }

    /** TODO 3: value of everything on the shelves, in centavos: add up product.stockValue() for every product. */
    public static int totalValue(List<Product> products) {
        return 0;
    }
}
