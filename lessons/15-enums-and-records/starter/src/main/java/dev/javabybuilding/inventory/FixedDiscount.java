package dev.javabybuilding.inventory;

/** Takes a fixed amount off (a coupon), but never more than the subtotal, so a sale never goes below zero. */
public class FixedDiscount implements Discount {

    private final int amountCentavos;

    public FixedDiscount(int amountCentavos) {
        this.amountCentavos = Math.max(0, amountCentavos);
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
