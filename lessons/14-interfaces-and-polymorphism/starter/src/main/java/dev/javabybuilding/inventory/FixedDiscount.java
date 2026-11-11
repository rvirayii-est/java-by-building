package dev.javabybuilding.inventory;

/** Takes a fixed amount off (a coupon), but never more than the subtotal, so a sale never goes below zero. */
public class FixedDiscount implements Discount {

    private final int amountCentavos;

    public FixedDiscount(int amountCentavos) {
        this.amountCentavos = Math.max(0, amountCentavos);
    }

    /** TODO 2: the coupon amount, but never more than the subtotal (Math.min). */
    @Override
    public int discountFor(int subtotalCentavos) {
        return -1;
    }

    @Override
    public String label() {
        return PriceCalculator.format(amountCentavos) + " off";
    }
}
