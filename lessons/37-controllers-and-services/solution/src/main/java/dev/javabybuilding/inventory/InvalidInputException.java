package dev.javabybuilding.inventory;

/** A value is not allowed: a blank name, a price of 0, a negative quantity, a duplicate product. */
public class InvalidInputException extends ShopException {

    public InvalidInputException(String message) {
        super(message);
    }
}
