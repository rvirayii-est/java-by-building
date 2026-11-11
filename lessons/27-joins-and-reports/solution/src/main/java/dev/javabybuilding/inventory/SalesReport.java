package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Questions about stock and sales, answered with streams. A stream is a pipeline: start from a collection,
 * filter and transform it step by step, and finish with one result. The original collection is never changed.
 * Money is BigDecimal pesos (lesson 20; before that int centavos).
 */
public class SalesReport {

    /** Products that are LOW or OUT: filter keeps only the ones the test says yes to. */
    public static List<Product> needsRestock(List<Product> products) {
        return products.stream()
                .filter(product -> !product.status().equals("OK"))
                .toList();
    }

    /** Just the names, in the same order: map turns each product into something else (its name). */
    public static List<String> names(List<Product> products) {
        return products.stream()
                .map(Product::getName)
                .toList();
    }

    /** The names as one line of text, for example "Rice 25kg, Sugar 1kg". An empty list gives "". */
    public static String namesAsText(List<Product> products) {
        return products.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));
    }

    /** Total money taken in for each payment method, in pesos. Methods nobody used are not in the map. */
    public static Map<PaymentMethod, BigDecimal> revenueByPayment(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getPayment,
                        () -> new TreeMap<>(), // a TreeMap sorts enum keys in the order they are written in the enum
                        Collectors.reducing(Money.ZERO, Sale::total, BigDecimal::add))); // BigDecimal cannot use summingInt
    }

    /** How many sales each customer made, by customer name. */
    public static Map<String, Long> salesPerCustomer(List<Sale> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(sale -> sale.getCustomer().getName(), TreeMap::new, Collectors.counting()));
    }

    /**
     * The sale with the biggest total. An Optional is a box that may be empty: an empty list of sales has no biggest one,
     * and Optional says so honestly instead of returning null.
     */
    public static Optional<Sale> biggestSale(List<Sale> sales) {
        return sales.stream().max(Comparator.comparing(Sale::total)); // BigDecimal is Comparable: comparing, not comparingInt
    }

    /** The average sale total in pesos (rounded to the centavo), or an empty Optional when there are no sales. */
    public static Optional<BigDecimal> averageSaleTotal(List<Sale> sales) {
        if (sales.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(totalRevenue(sales).divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP));
    }

    /** Sum of the money in every sale: map each sale to its total, then add them up starting from 0.00. */
    public static BigDecimal totalRevenue(List<Sale> sales) {
        return sales.stream().map(Sale::total).reduce(Money.ZERO, BigDecimal::add);
    }
}
