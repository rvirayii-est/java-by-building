package dev.javabybuilding.inventory;

/**
 * One line of the stock log: "24 x Sugar 1kg RECEIVED". A record is a class for plain data: Java writes the
 * constructor, the accessors (productName(), type(), quantity()), equals, hashCode and toString for you.
 * Records never change after they are created.
 */
public record StockMovement(String productName, MovementType type, int quantity) {

    /** A compact constructor: runs before the fields are set, so a bad movement can never exist. */
    public StockMovement {
        // TODO 3: when productName is null or blank, throw new InvalidInputException("A stock movement needs a product name.");
        // when quantity is 0 or less, throw new InvalidInputException("A stock movement must be for at least 1 unit.").
    }

    /** The change to the shelf this movement made: positive when stock came in, negative when it went out. */
    public int signedQuantity() {
        return type.signedQuantity(quantity);
    }
}
