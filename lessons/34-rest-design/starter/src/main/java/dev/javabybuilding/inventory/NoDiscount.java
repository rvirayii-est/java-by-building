package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/** Full price: takes nothing off. */
public class NoDiscount implements Discount {

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return Money.ZERO;
    }

    @Override
    public String label() {
        return "No discount";
    }
}
