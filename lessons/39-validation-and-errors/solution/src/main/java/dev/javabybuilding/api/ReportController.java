package dev.javabybuilding.api;

import dev.javabybuilding.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The web layer for reports: read-only, nothing can go wrong with the request itself. */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/stock")
    public StockReportResponse stock() {
        return reports.stockReport();
    }

    @GetMapping("/sales")
    public SalesReportResponse sales() {
        return reports.salesReport();
    }
}
