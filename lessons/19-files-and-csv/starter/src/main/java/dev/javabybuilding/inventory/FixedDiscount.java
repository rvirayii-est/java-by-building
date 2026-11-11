package dev.javabybuilding.inventory;

/** Takes a fixed amount off (a coupon), but never more than the subtotal, so a sale never goes below zero. */
public class FixedDiscount implements Discount {

    private final int amountCentavos;

    /** @throws InvalidInputException when the amount is negative (lesson 14 quietly used 0) */
    public FixedDiscount(int amountCentavos) {
        if (amountCentavos < 0) {
            throw new InvalidInputException("A discount amount must not be negative.");
        }
        this.amountCentavos = amountCentavos;
    }

    public int getAmountCentavos() {
        return amountCentavos;
    }

    @Override
    public int discountFor(int subtotalCentavos) {
        return Math.min(amountCentavos, subtotalCentavos);
    }

    @Override
    public String label() {
        return PriceCalculator.format(amountCentavos) + " off";
    }
}
