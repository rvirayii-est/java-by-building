package dev.javabybuilding.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import dev.javabybuilding.service.ReportService;
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

    // TODO 3: describe this operation for the documentation:  @Operation(summary = "...")  (a short sentence of what it does)
    @GetMapping("/stock")
    public StockReportResponse stock() {
        return reports.stockReport();
    }

    // TODO 3: describe this operation for the documentation:  @Operation(summary = "...")  (a short sentence of what it does)
    @GetMapping("/sales")
    public SalesReportResponse sales() {
        return reports.salesReport();
    }
}
