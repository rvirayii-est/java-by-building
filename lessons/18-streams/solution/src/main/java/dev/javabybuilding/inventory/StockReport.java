package dev.javabybuilding.inventory;

import java.util.List;

public class StockReport {

    /** One header line, then one line per product, in the order of the list. */
    public static String build(List<Product> products) {
        StringBuilder report = new StringBuilder(String.format("%-14s%5s  %s", "Product", "Qty", "Status"));
        for (Product product : products) {
            report.append("\n").append(String.format("%-14s%5d  %s", product.getName(), product.getQuantity(), product.status()));
        }
        return report.toString();
    }

    /** Value of everything on the shelves, in centavos: the sum of every product's stock value. */
    public static int totalValue(List<Product> products) {
        return products.stream().mapToInt(Product::stockValue).sum(); // lesson 18: the loop became a stream
    }
}
