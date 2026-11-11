package dev.javabybuilding.inventory;

/** Why stock changed. */
public enum MovementType {
    RECEIVED, // stock came in (a delivery)
    SOLD;     // stock went out (a sale)

    /** The change to the shelf: +quantity for RECEIVED, -quantity for SOLD. */
    public int signedQuantity(int quantity) {
        // TODO 1: return switch (this) { case RECEIVED -> ...; case SOLD -> ...; };
        // A switch expression gives a value, and the compiler checks every constant is covered.
        return 0;
    }
}
