package dev.javabybuilding.inventory;

/** Full price: takes nothing off. */
public class NoDiscount implements Discount {

    @Override
    public int discountFor(int subtotalCentavos) {
        return 0;
    }

    @Override
    public String label() {
        return "No discount";
    }
}
