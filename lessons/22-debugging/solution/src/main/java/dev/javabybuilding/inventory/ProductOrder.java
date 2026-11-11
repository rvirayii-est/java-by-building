package dev.javabybuilding.inventory;

import java.util.Comparator;

/**
 * Ready-made orders for sorting products. A Comparator<Product> answers one question: "which of these two comes first?"
 * Comparator.comparing(...) builds one from a method that picks the value to compare.
 */
public class ProductOrder {

    /** A to Z by name, ignoring capitals. */
    public static final Comparator<Product> BY_NAME = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);

    /** Fewest units first: the shelves that need attention first. */
    public static final Comparator<Product> BY_QUANTITY = Comparator.comparingInt(Product::getQuantity);

    /** The most valuable stock first. Equal values fall back to A to Z, so the order is always the same. */
    public static final Comparator<Product> BY_STOCK_VALUE_DESC =
            Comparator.comparing(Product::stockValue).reversed().thenComparing(BY_NAME);
}
