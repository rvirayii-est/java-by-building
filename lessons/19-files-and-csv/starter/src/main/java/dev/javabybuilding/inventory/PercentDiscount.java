package dev.javabybuilding.inventory;

/** Takes a percentage (0 to 100) off, rounded down to a whole centavo. */
public class PercentDiscount implements Discount {

    private final int percent;

    /** @throws InvalidInputException unless percent is from 0 to 100 (lesson 14 quietly clamped it) */
    public PercentDiscount(int percent) {
        if (percent < 0 || percent > 100) {
            throw new InvalidInputException("A percentage must be from 0 to 100.");
        }
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    @Override
    public int discountFor(int subtotalCentavos) {
        return (int) ((long) subtotalCentavos * percent / 100); // long: subtotal x percent can be too big for an int
    }

    @Override
    public String label() {
        return percent + "% off";
    }
}
