package dev.javabybuilding.api;

import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.ProductInUseException;
import dev.javabybuilding.inventory.ShopException;
import dev.javabybuilding.inventory.UnknownProductException;

/**
 * The ONE place that decides which HTTP status goes with which problem. The shop code throws exceptions; the API layer
 * turns each kind into a number. (In lesson 39 Spring's @ControllerAdvice plays this role.)
 *
 * <table>
 * <caption>Problem to status</caption>
 * <tr><td>UnknownProductException, UnknownSaleException</td><td>404 Not Found</td></tr>
 * <tr><td>InvalidInputException</td><td>400 Bad Request</td></tr>
 * <tr><td>InsufficientStockException, ProductAlreadyExistsException, ProductInUseException</td><td>409 Conflict</td></tr>
 * <tr><td>any other ShopException</td><td>400 Bad Request</td></tr>
 * <tr><td>anything else (a bug)</td><td>500 Internal Server Error</td></tr>
 * </table>
 */
public final class ErrorMapper {

    private ErrorMapper() {}

    public static int statusFor(Throwable problem) {
        // TODO 2: follow the table above with instanceof tests, most specific first:
        // UnknownProductException and UnknownSaleException give 404; InsufficientStockException, ProductAlreadyExistsException
        // and ProductInUseException give 409; InvalidInputException and any other ShopException give 400; everything else 500.
        return 0;
    }
}
