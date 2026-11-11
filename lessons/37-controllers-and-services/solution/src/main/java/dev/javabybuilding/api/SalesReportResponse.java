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
        Map<String, BigDecimal> byPayment = new LinkedHashMap<>(); // keeps CASH, GCASH, CARD order
        SalesReport.revenueByPayment(sales).forEach((method, amount) -> byPayment.put(method.name(), amount));
        return new SalesReportResponse(
                sales.size(),
                SalesReport.totalRevenue(sales),
                SalesReport.averageSaleTotal(sales).orElse(Money.ZERO),
                byPayment,
                SalesReport.biggestSale(sales).map(Sale::getNumber).orElse(null));
    }
}
