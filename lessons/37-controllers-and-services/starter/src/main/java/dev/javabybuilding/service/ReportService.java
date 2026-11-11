package dev.javabybuilding.service;

import dev.javabybuilding.api.SalesReportResponse;
import dev.javabybuilding.api.StockReportResponse;
import dev.javabybuilding.inventory.ShopService;
import org.springframework.stereotype.Service;

/** The two reports. Read-only: they change nothing. */
@Service
public class ReportService {

    private final ShopService shop;

    public ReportService(ShopService shop) {
        this.shop = shop;
    }

    public StockReportResponse stockReport() {
        synchronized (shop) {
            return StockReportResponse.from(shop.products(), shop.reorderSuggestions());
        }
    }

    public SalesReportResponse salesReport() {
        synchronized (shop) {
            return SalesReportResponse.from(shop.sales());
        }
    }
}
