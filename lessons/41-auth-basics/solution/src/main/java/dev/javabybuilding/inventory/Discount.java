package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/**
 * A promise: "any class that implements Discount can work out how much to take off a sale".
 * An interface lists WHAT must be possible (the method signatures), not HOW. Sale only knows this promise, so
 * it works with every kind of discount, including ones written later.
 */
public interface Discount {

    /** How much to take off a subtotal, in pesos (never more than the subtotal, never negative). */
    BigDecimal discountFor(BigDecimal subtotal);

    /** Short text for receipts, for example "10% off". */
    String label();
}
