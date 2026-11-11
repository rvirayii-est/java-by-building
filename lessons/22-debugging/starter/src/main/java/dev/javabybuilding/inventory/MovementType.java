package dev.javabybuilding.inventory;

/** Why stock changed. */
public enum MovementType {
    RECEIVED, // stock came in (a delivery)
    SOLD;     // stock went out (a sale)

    /** The change to the shelf: +quantity for RECEIVED, -quantity for SOLD. */
    public int signedQuantity(int quantity) {
        return switch (this) { // a switch expression gives a value; the compiler checks every constant is covered
            case RECEIVED -> quantity;
            case SOLD -> quantity;
        };
    }
}
