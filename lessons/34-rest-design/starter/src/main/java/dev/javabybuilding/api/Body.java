package dev.javabybuilding.api;

import dev.javabybuilding.inventory.InvalidInputException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Reads fields out of a request body (a Map) and complains in plain English when one is missing or has the wrong kind of
 * value. Writing this by hand for every field of every request is exactly the chore lesson 35 (Jackson) and lesson 39
 * (validation) take away.
 */
final class Body {

    private final Map<String, Object> values;

    Body(Map<String, Object> values) {
        this.values = values == null ? Map.of() : values;
    }

    /** A required text field. */
    String text(String field) {
        Object value = values.get(field);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new InvalidInputException("Missing or empty field: " + field);
        }
        return text;
    }

    /** A text field that may be left out (null when it is). */
    String optionalText(String field) {
        Object value = values.get(field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String text)) {
            throw new InvalidInputException(field + " must be text");
        }
        return text;
    }

    /** A required whole number. */
    int integer(String field) {
        // TODO 3: the value must be an Integer (values.get(field) instanceof Integer); otherwise throw
        // new InvalidInputException(field + " must be a whole number").
        throw new UnsupportedOperationException("TODO 3");
    }

    /** A required amount of money. A number or a text such as "98.50" is accepted. */
    BigDecimal decimal(String field) {
        // TODO 3 (continued): a Number becomes new BigDecimal(number.toString()); a String becomes new BigDecimal(text.trim());
        // anything else, or text that is not a number (NumberFormatException), throws new InvalidInputException(field + " must be a number").
        throw new UnsupportedOperationException("TODO 3");
    }
}
