package dev.javabybuilding.api;

import dev.javabybuilding.inventory.ShopException;

/**
 * Creating a product whose name is taken. The shop core reports this as a plain InvalidInputException (a 400), but for an
 * API it is a CONFLICT with what already exists (a 409), so the API layer checks first and throws this one.
 */
public class ProductAlreadyExistsException extends ShopException {

    public ProductAlreadyExistsException(String name) {
        super("That product already exists: " + name);
    }
}
