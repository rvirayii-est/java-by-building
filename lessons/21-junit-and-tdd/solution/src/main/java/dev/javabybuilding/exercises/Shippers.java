package dev.javabybuilding.exercises;

import java.util.List;

/** Lesson 14, exercise 2: helpers that work on any list of Shipping options. */
public class Shippers {

    /** The lowest fee among the options for this weight, or -1 when the list is empty. */
    public static int cheapestFee(List<Shipping> options, int kilograms) {
        int cheapest = -1;
        for (Shipping option : options) {
            int fee = option.fee(kilograms);
            if (cheapest == -1 || fee < cheapest) {
                cheapest = fee;
            }
        }
        return cheapest;
    }

    /** The label of the cheapest option (the first one when tied), or "none" when the list is empty. */
    public static String cheapestLabel(List<Shipping> options, int kilograms) {
        Shipping best = null;
        for (Shipping option : options) {
            if (best == null || option.fee(kilograms) < best.fee(kilograms)) {
                best = option;
            }
        }
        if (best == null) {
            return "none";
        }
        return best.label();
    }
}
