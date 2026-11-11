package dev.javabybuilding.inventory;

/**
 * The parent of every problem the shop code can report. It extends RuntimeException, so it is an "unchecked"
 * exception: the compiler does not force every method to declare it. One catch (ShopException e) handles them all.
 */
public class ShopException extends RuntimeException {

    public ShopException(String message) {
        super(message); // getMessage() returns this text
    }
}
