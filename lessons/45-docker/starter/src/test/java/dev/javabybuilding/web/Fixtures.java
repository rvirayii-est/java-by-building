package dev.javabybuilding.web;

import dev.javabybuilding.api.ProductResponse;
import dev.javabybuilding.api.SaleLineResponse;
import dev.javabybuilding.api.SaleResponse;
import dev.javabybuilding.inventory.Money;
import java.time.LocalDateTime;
import java.util.List;

/** Ready-made answers for the tests: one place, so a test says WHAT it checks and not how a product is built. */
final class Fixtures {

    private Fixtures() {}

    static ProductResponse rice() {
        return new ProductResponse("Rice 25kg", Money.of("1250.50"), 12, 10, "OK", Money.of("15006.00"));
    }

    static ProductResponse oil() {
        return new ProductResponse("Cooking Oil 1L", Money.of("98.00"), 4, 10, "LOW", Money.of("392.00"));
    }

    /** Sale number 1: two bags of rice, paid in cash by a walk-in, on 1 March 2026 at 09:30. */
    static SaleResponse saleOfTwoRice() {
        return new SaleResponse(1, LocalDateTime.of(2026, 3, 1, 9, 30), "Walk-in",
                List.of(new SaleLineResponse("Rice 25kg", Money.of("1250.50"), 2, Money.of("2501.00"))),
                "No discount", Money.of("2501.00"), Money.ZERO, Money.of("2501.00"), "CASH");
    }
}
