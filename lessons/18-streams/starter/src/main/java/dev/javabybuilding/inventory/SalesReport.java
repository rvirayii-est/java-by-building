package dev.javabybuilding.inventory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Questions about stock and sales, answered with streams. A stream is a pipeline: start from a collection,
 * filter and transform it step by step, and finish with one result. The original collection is never changed.
 * Money is whole centavos (int), as always.
 */
public class SalesReport {

    /** Products that are LOW or OUT: filter keeps only the ones the test says yes to. */
    public static List<Product> needsRestock(List<Product> products) {
        // TODO 1: keep only the products whose status() is not "OK": products.stream().filter(...).toList()
        return List.of();
    }

    /** Just the names, in the same order: map turns each product into something else (its name). */
    public static List<String> names(List<Product> products) {
        // TODO 2: map each product to its name: products.stream().map(Product::getName).toList()
        return List.of();
    }

    /** The names as one line of text, for example "Rice 25kg, Sugar 1kg". An empty list gives "". */
    public static String namesAsText(List<Product> products) {
        // TODO 3: the same, then .collect(Collectors.joining(", ")) instead of toList()
        return "";
    }

    /** Total money taken in for each payment method, in centavos. Methods nobody used are not in the map. */
    public static Map<PaymentMethod, Integer> revenueByPayment(List<Sale> sales) {
        // TODO 4: sales.stream().collect(Collectors.groupingBy(Sale::getPayment, () -> new TreeMap<>(), Collectors.summingInt(Sale::total)))
        // (the TreeMap makes the keys come out in the order they are written in the PaymentMethod enum)
        return new TreeMap<>();
    }

    /** How many sales each customer made, by customer name. */
    public static Map<String, Long> salesPerCustomer(List<Sale> sales) {
        // TODO 5: sales.stream().collect(Collectors.groupingBy(sale -> sale.getCustomer().getName(), TreeMap::new, Collectors.counting()))
        return new TreeMap<>();
    }

    /**
     * The sale with the biggest total. An Optional is a box that may be empty: an empty list of sales has no biggest one,
     * and Optional says so honestly instead of returning null.
     */
    public static Optional<Sale> biggestSale(List<Sale> sales) {
        // TODO 6: sales.stream().max(Comparator.comparingInt(Sale::total))
        return Optional.empty();
    }

    /** The average sale total in centavos, or an empty OptionalDouble when there are no sales. */
    public static OptionalDouble averageSaleTotal(List<Sale> sales) {
        // TODO 7: sales.stream().mapToInt(Sale::total).average()
        return OptionalDouble.empty();
    }

    /** Sum of the money in every sale (a stream of ints: mapToInt, then sum). */
    public static int totalRevenue(List<Sale> sales) {
        // TODO 8: sales.stream().mapToInt(Sale::total).sum()
        return 0;
    }
}
