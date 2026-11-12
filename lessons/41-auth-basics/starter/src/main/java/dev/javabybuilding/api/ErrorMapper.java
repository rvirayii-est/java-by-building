package dev.javabybuilding.api;

import dev.javabybuilding.inventory.DatabaseException;
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
 * <tr><td>DatabaseException</td><td>500 Internal Server Error</td></tr>
 * <tr><td>any other ShopException</td><td>400 Bad Request</td></tr>
 * <tr><td>anything else (a bug)</td><td>500 Internal Server Error</td></tr>
 * </table>
 */
public final class ErrorMapper {

    private ErrorMapper() {}

    public static int statusFor(Throwable problem) {
        if (problem instanceof UnknownProductException || problem instanceof UnknownSaleException) {
            return 404;
        }
        if (problem instanceof InsufficientStockException || problem instanceof ProductAlreadyExistsException
                || problem instanceof ProductInUseException) {
            return 409;
        }
        if (problem instanceof DatabaseException) {
            return 500; // the database failed: not the caller's fault (lesson 39)
        }
        if (problem instanceof InvalidInputException || problem instanceof ShopException) {
            return 400;
        }
        return 500;
    }
}
