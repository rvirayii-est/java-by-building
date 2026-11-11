package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/** Takes a fixed amount off (a coupon), but never more than the subtotal, so a sale never goes below zero. */
public class FixedDiscount implements Discount {

    private final BigDecimal amount;

    /** @throws InvalidInputException when the amount is negative */
    public FixedDiscount(BigDecimal amount) {
        if (amount.signum() < 0) { // signum is -1, 0 or 1: the sign of the number
            throw new InvalidInputException("A discount amount must not be negative.");
        }
        this.amount = Money.round(amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return amount.min(subtotal);
    }

    @Override
    public String label() {
        return PriceCalculator.format(amount) + " off";
    }
}
