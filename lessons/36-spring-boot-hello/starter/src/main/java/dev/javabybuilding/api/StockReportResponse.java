package dev.javabybuilding.api;

import dev.javabybuilding.inventory.Product;
import dev.javabybuilding.inventory.ReorderSuggestion;
import dev.javabybuilding.inventory.StockReport;
import java.math.BigDecimal;
import java.util.List;

/** The stock report (lesson 03) as data: every product, the value of the shelves and what to reorder. */
public record StockReportResponse(List<ProductResponse> products, BigDecimal totalValue, List<Reorder> reorder) {

    /** One line of the reorder list: "order 16 more of Cooking Oil 1L". */
    public record Reorder(String product, int quantity) {}

    public static StockReportResponse from(List<Product> products, List<ReorderSuggestion> suggestions) {
        return new StockReportResponse(
                products.stream().map(ProductResponse::from).toList(),
                StockReport.totalValue(products),
                suggestions.stream().map(s -> new Reorder(s.productName(), s.quantity())).toList());
    }
}
