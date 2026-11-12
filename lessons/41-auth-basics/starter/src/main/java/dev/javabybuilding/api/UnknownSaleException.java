package dev.javabybuilding.api;

import dev.javabybuilding.inventory.ShopException;

/** There is no sale with that number. */
public class UnknownSaleException extends ShopException {

    public UnknownSaleException(int number) {
        super("Unknown sale: #" + number);
    }
}
