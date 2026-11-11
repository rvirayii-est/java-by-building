package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;

/**
 * Lesson 39 drills: validation and error answers, written by hand in plain Java (no annotations). Complete each TODO until
 * Exercises39Test is green.
 */
public class ValidationDrills {

    /**
     * Is this a Philippine mobile number? Either 11 digits starting with 09 ("09171234567") or "+63" followed by 10 digits that
     * start with 9 ("+639171234567"). Nothing else: no spaces, no dashes, no letters. null is not valid.
     */
    public static boolean isValidPhone(String phone) {
        // TODO 1: phone != null && phone.matches("...") with a regular expression: 09\d{9}  or  \+639\d{9}.
        return false;
    }

    /**
     * Checks the three fields of a new product, given as the text the client sent, and returns the problems, at most ONE per field
     * (the first rule that fails), in the order name, price, reorderLevel. A valid product gives an empty list.
     * <ul>
     *   <li>name: blank or null is "name: is required"; longer than 60 characters is "name: must be at most 60 characters";</li>
     *   <li>price: blank or null is "price: is required"; not a number is "price: must be a number"; below 0.01 is
     *       "price: must be at least 0.01"; above 10000.00 is "price: must be at most 10000.00"; more than 2 decimals is
     *       "price: must have at most 2 decimals";</li>
     *   <li>reorderLevel: blank or null is "reorderLevel: is required"; not a whole number is "reorderLevel: must be a whole number";
     *       below 0 is "reorderLevel: must be 0 or more".</li>
     * </ul>
     */
    public static List<String> validateProduct(String name, String price, String reorderLevel) {
        // TODO 2: an ArrayList of problems. For the price use new BigDecimal(text.trim()) in a try/catch (NumberFormatException) and
        // compareTo; the number of decimals is value.stripTrailingZeros().scale() (negative scales mean 0 decimals).
        return null;
    }

    /**
     * The status for the name of an exception class: UnknownProductException is 404; InvalidInputException is 400;
     * InsufficientStockException, ProductAlreadyExistsException and ProductInUseException are 409; DatabaseException and anything
     * else are 500.
     */
    public static int statusFor(String exceptionName) {
        // TODO 3: a switch on the name.
        return 0;
    }

    /**
     * The error answer as JSON text, in this order and with no spaces:
     * {"status":404,"error":"Not Found","message":"Unknown product: salt","path":"/products/salt"}.
     * Quotes and backslashes inside the texts must be escaped (\" and \\).
     */
    public static String errorJson(int status, String error, String message, String path) {
        // TODO 4: build the text with +, escaping each of the three texts with s.replace("\\", "\\\\").replace("\"", "\\\"")
        // (backslash first).
        return null;
    }

    /**
     * Groups problems written as "field: message" by field, keeping the order in which the fields first appear:
     * ["name: is required", "price: bad", "name: too long"] gives {name=[is required, too long], price=[bad]}.
     *
     * @throws IllegalArgumentException when a problem has no ": "
     */
    public static Map<String, List<String>> groupByField(List<String> problems) {
        // TODO 5: a LinkedHashMap; for each problem split at the FIRST ": " (indexOf) and use computeIfAbsent(field, k -> new ArrayList<>()).
        return null;
    }
}
