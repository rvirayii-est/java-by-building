package dev.javabybuilding.inventory;

/** Takes a percentage off, rounded down to a whole centavo. The percentage is kept between 0 and 100. */
public class PercentDiscount implements Discount {

    private final int percent;

    public PercentDiscount(int percent) {
        this.percent = Math.max(0, Math.min(100, percent)); // shortcut: bad values are clamped; lesson 16 shows how to refuse them with an exception
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
