package dev.javabybuilding.api;

import dev.javabybuilding.inventory.SaleLine;
import java.math.BigDecimal;

/** One line of a sale as the API shows it. */
public record SaleLineResponse(String product, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {

    public static SaleLineResponse from(SaleLine line) {
        return new SaleLineResponse(line.productName(), line.unitPrice(), line.quantity(), line.lineTotal());
    }
}
