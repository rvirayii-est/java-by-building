package dev.javabybuilding.api;

import dev.javabybuilding.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The web layer for reports: read-only, nothing can go wrong with the request itself. */
@RestController
@Tag(name = "Reports", description = "Read-only reports. Admin only.")
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @Operation(summary = "The stock report: every product with its status and the reorder suggestions")
    @GetMapping("/stock")
    public StockReportResponse stock() {
        return reports.stockReport();
    }

    @Operation(summary = "The products that need attention (status OUT first, then LOW)")
    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock() {
        return reports.lowStock();
    }

    @Operation(summary = "The sales report: count, revenue, average, revenue by payment method")
    @GetMapping("/sales")
    public SalesReportResponse sales() {
        return reports.salesReport();
    }
}
