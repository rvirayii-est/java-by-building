package dev.javabybuilding.service;

import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.api.SalesReportResponse;
import dev.javabybuilding.api.StockReportResponse;
import dev.javabybuilding.inventory.Product;
import dev.javabybuilding.inventory.ShopService;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/** The two reports. Read-only: they change nothing. */
@Service
public class ReportService {

    private final ShopService shop;

    public ReportService(ShopService shop) {
        this.shop = shop;
    }

    public StockReportResponse stockReport() {
        return StockReportResponse.from(shop.products(), shop.reorderSuggestions());
    }

    /**
     * The products that need attention: status OUT first, then LOW, each group in name order. Lesson 40 set this as the checkpoint of
     * Week 10; lesson 48 shows how a new feature touches every layer: service, controller, design table, documentation, rules, tests.
     */
    public List<ProductResponse> lowStock() {
        // TODO 1: the products whose status is not "OK", OUT before LOW, each group in name order, as ProductResponse (ProductResponse::from).
        // Hint: filter(product -> !product.status().equals("OK")), sorted(Comparator.comparing((Product product) -> product.status().equals("OUT") ? 0 : 1).thenComparing(Product::getName)).
        return List.of();
    }

    public SalesReportResponse salesReport() {
        return SalesReportResponse.from(shop.sales());
    }
}
