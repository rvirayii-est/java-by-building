package dev.javabybuilding.inventory;

/**
 * One line of the stock log: "24 x Sugar 1kg RECEIVED". A record is a class for plain data: Java writes the
 * constructor, the accessors (productName(), type(), quantity()), equals, hashCode and toString for you.
 * Records never change after they are created.
 */
public record StockMovement(String productName, MovementType type, int quantity) {

    /** The change to the shelf this movement made: positive when stock came in, negative when it went out. */
    public int signedQuantity() {
        // TODO 3: let the type decide: return type.signedQuantity(quantity);
        return 0;
    }
}
