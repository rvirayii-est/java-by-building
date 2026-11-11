package dev.javabybuilding.api;

import dev.javabybuilding.inventory.Money;
import dev.javabybuilding.inventory.Sale;
import dev.javabybuilding.inventory.SalesReport;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The sales report as data. {@code biggestSale} is the NUMBER of the sale with the largest total, or null when there are
 * no sales yet. {@code revenueByPayment} lists only the payment methods somebody used.
 */
public record SalesReportResponse(int salesCount, BigDecimal revenue, BigDecimal averageSale,
                                  Map<String, BigDecimal> revenueByPayment, Integer biggestSale) {

    public static SalesReportResponse from(List<Sale> sales) {
        // TODO 5: salesCount = sales.size(); revenue = SalesReport.totalRevenue(sales); averageSale = SalesReport.averageSaleTotal(sales)
        // (an Optional: orElse(Money.ZERO)); revenueByPayment = SalesReport.revenueByPayment(sales) with each PaymentMethod
        // turned into its name() (put them into a LinkedHashMap to keep the order); biggestSale = the NUMBER of
        // SalesReport.biggestSale(sales), or null when there are no sales (map(Sale::getNumber).orElse(null)).
        throw new UnsupportedOperationException("TODO 5");
    }
}
