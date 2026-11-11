package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        return phone != null && phone.matches("09\\d{9}|\\+639\\d{9}");
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
        List<String> problems = new ArrayList<>();
        if (name == null || name.isBlank()) {
            problems.add("name: is required");
        } else if (name.length() > 60) {
            problems.add("name: must be at most 60 characters");
        }
        if (price == null || price.isBlank()) {
            problems.add("price: is required");
        } else {
            try {
                BigDecimal value = new BigDecimal(price.trim());
                if (value.compareTo(new BigDecimal("0.01")) < 0) {
                    problems.add("price: must be at least 0.01");
                } else if (value.compareTo(new BigDecimal("10000.00")) > 0) {
                    problems.add("price: must be at most 10000.00");
                } else if (value.stripTrailingZeros().scale() > 2) {
                    problems.add("price: must have at most 2 decimals");
                }
            } catch (NumberFormatException e) {
                problems.add("price: must be a number");
            }
        }
        if (reorderLevel == null || reorderLevel.isBlank()) {
            problems.add("reorderLevel: is required");
        } else {
            try {
                if (Integer.parseInt(reorderLevel.trim()) < 0) {
                    problems.add("reorderLevel: must be 0 or more");
                }
            } catch (NumberFormatException e) {
                problems.add("reorderLevel: must be a whole number");
            }
        }
        return problems;
    }

    /**
     * The status for the name of an exception class: UnknownProductException is 404; InvalidInputException is 400;
     * InsufficientStockException, ProductAlreadyExistsException and ProductInUseException are 409; DatabaseException and anything
     * else are 500.
     */
    public static int statusFor(String exceptionName) {
        return switch (exceptionName) {
            case "UnknownProductException" -> 404;
            case "InvalidInputException" -> 400;
            case "InsufficientStockException", "ProductAlreadyExistsException", "ProductInUseException" -> 409;
            default -> 500;
        };
    }

    /**
     * The error answer as JSON text, in this order and with no spaces:
     * {"status":404,"error":"Not Found","message":"Unknown product: salt","path":"/products/salt"}.
     * Quotes and backslashes inside the texts must be escaped (\" and \\).
     */
    public static String errorJson(int status, String error, String message, String path) {
        return "{\"status\":" + status + ",\"error\":\"" + escape(error) + "\",\"message\":\"" + escape(message)
                + "\",\"path\":\"" + escape(path) + "\"}";
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Groups problems written as "field: message" by field, keeping the order in which the fields first appear:
     * ["name: is required", "price: bad", "name: too long"] gives {name=[is required, too long], price=[bad]}.
     *
     * @throws IllegalArgumentException when a problem has no ": "
     */
    public static Map<String, List<String>> groupByField(List<String> problems) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (String problem : problems) {
            int split = problem.indexOf(": ");
            if (split < 0) {
                throw new IllegalArgumentException("Not a field problem: " + problem);
            }
            grouped.computeIfAbsent(problem.substring(0, split), field -> new ArrayList<>()).add(problem.substring(split + 2));
        }
        return grouped;
    }
}
