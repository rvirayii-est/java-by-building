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
        // TODO 4: start with best = new NoDiscount(); loop over options; when option.discountFor(subtotalCentavos)
        // is bigger than best.discountFor(subtotalCentavos), best = option. Return best.
        return null;
    }
}
