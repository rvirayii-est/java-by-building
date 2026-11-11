package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/**
 * The 20% discount for senior citizens and persons with disability (PWD).
 * shortcut: the real rule has more conditions (which items, a purchase cap, ID checks); this teaches the idea only.
 * It reuses PercentDiscount (composition) instead of copying its arithmetic.
 */
public class SeniorPwdDiscount implements Discount {

    private final PercentDiscount twentyPercent = new PercentDiscount(20);

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return twentyPercent.discountFor(subtotal);
    }

    @Override
    public String label() {
        return "Senior/PWD 20%";
    }
}
