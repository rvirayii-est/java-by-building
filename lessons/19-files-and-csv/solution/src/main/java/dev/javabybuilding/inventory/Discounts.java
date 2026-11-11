package dev.javabybuilding.inventory;

import java.util.List;

/** Helpers that work on any list of Discount objects, whatever their classes. */
public class Discounts {

    /**
     * The discount that takes the most off this subtotal (the first one when tied). Each call to
     * discountFor runs the version in that object's own class: that is polymorphism.
     * An empty list gives NoDiscount.
     */
    public static Discount best(List<Discount> options, int subtotalCentavos) {
        Discount best = new NoDiscount();
        for (Discount option : options) {
            if (option.discountFor(subtotalCentavos) > best.discountFor(subtotalCentavos)) {
                best = option;
            }
        }
        return best;
    }
}
