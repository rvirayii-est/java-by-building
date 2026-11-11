package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercises39Test {

    @Test
    void goodPhoneNumbersAreElevenDigitsFromZeroNineOrPlusSixtyThree() {
        assertTrue(ValidationDrills.isValidPhone("09171234567"));
        assertTrue(ValidationDrills.isValidPhone("+639171234567"));
    }

    @Test
    void otherPhoneNumbersAreRefused() {
        assertFalse(ValidationDrills.isValidPhone(null));
        assertFalse(ValidationDrills.isValidPhone(""));
        assertFalse(ValidationDrills.isValidPhone("0917123456"));     // too short
        assertFalse(ValidationDrills.isValidPhone("091712345678"));   // too long
        assertFalse(ValidationDrills.isValidPhone("0817123456 7"));   // wrong start and a space
        assertFalse(ValidationDrills.isValidPhone("+63 917 123 4567"));
        assertFalse(ValidationDrills.isValidPhone("0917-123-4567"));
        assertFalse(ValidationDrills.isValidPhone("+638171234567"));
        assertFalse(ValidationDrills.isValidPhone("09abcdefghi"));
    }

    @Test
    void aGoodProductHasNoProblems() {
        assertEquals(List.of(), ValidationDrills.validateProduct("Salt 1kg", "20.00", "5"));
        assertEquals(List.of(), ValidationDrills.validateProduct("Free sample", "0.01", "0"));
        assertEquals(List.of(), ValidationDrills.validateProduct("Gold", " 10000 ", " 3 "));
        assertEquals(List.of(), ValidationDrills.validateProduct("Salt", "20.500", "1")); // 20.500 is 20.5: one decimal
    }

    @Test
    void missingFieldsAreAllReportedInTheOrderOfTheFields() {
        assertEquals(List.of("name: is required", "price: is required", "reorderLevel: is required"),
                ValidationDrills.validateProduct(null, null, null));
        assertEquals(List.of("name: is required", "price: is required", "reorderLevel: is required"),
                ValidationDrills.validateProduct("  ", "", " "));
    }

    @Test
    void theNameHasALimit() {
        assertEquals(List.of(), ValidationDrills.validateProduct("x".repeat(60), "1", "1"));
        assertEquals(List.of("name: must be at most 60 characters"), ValidationDrills.validateProduct("x".repeat(61), "1", "1"));
    }

    @Test
    void thePriceMustBeANumberInRangeWithTwoDecimals() {
        assertEquals(List.of("price: must be a number"), ValidationDrills.validateProduct("A", "abc", "1"));
        assertEquals(List.of("price: must be at least 0.01"), ValidationDrills.validateProduct("A", "0", "1"));
        assertEquals(List.of("price: must be at least 0.01"), ValidationDrills.validateProduct("A", "-5", "1"));
        assertEquals(List.of("price: must be at most 10000.00"), ValidationDrills.validateProduct("A", "10000.01", "1"));
        assertEquals(List.of("price: must have at most 2 decimals"), ValidationDrills.validateProduct("A", "12.345", "1"));
    }

    @Test
    void theReorderLevelMustBeAWholeNumberOfZeroOrMore() {
        assertEquals(List.of("reorderLevel: must be a whole number"), ValidationDrills.validateProduct("A", "1", "1.5"));
        assertEquals(List.of("reorderLevel: must be a whole number"), ValidationDrills.validateProduct("A", "1", "many"));
        assertEquals(List.of("reorderLevel: must be 0 or more"), ValidationDrills.validateProduct("A", "1", "-1"));
    }

    @Test
    void severalProblemsComeTogetherOnePerField() {
        assertEquals(List.of("name: is required", "price: must be at least 0.01", "reorderLevel: must be 0 or more"),
                ValidationDrills.validateProduct("", "0", "-4"));
    }

    @Test
    void exceptionNamesMapToStatuses() {
        assertEquals(404, ValidationDrills.statusFor("UnknownProductException"));
        assertEquals(400, ValidationDrills.statusFor("InvalidInputException"));
        assertEquals(409, ValidationDrills.statusFor("InsufficientStockException"));
        assertEquals(409, ValidationDrills.statusFor("ProductAlreadyExistsException"));
        assertEquals(409, ValidationDrills.statusFor("ProductInUseException"));
        assertEquals(500, ValidationDrills.statusFor("DatabaseException"));
        assertEquals(500, ValidationDrills.statusFor("NullPointerException"));
    }

    @Test
    void errorJsonHasTheFourFieldsInOrder() {
        assertEquals("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Unknown product: salt\",\"path\":\"/products/salt\"}",
                ValidationDrills.errorJson(404, "Not Found", "Unknown product: salt", "/products/salt"));
    }

    @Test
    void errorJsonEscapesQuotesAndBackslashes() {
        assertEquals("{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Unknown product: \\\"Milo\\\"\",\"path\":\"C:\\\\x\"}",
                ValidationDrills.errorJson(400, "Bad Request", "Unknown product: \"Milo\"", "C:\\x"));
    }

    @Test
    void groupByFieldKeepsTheOrderOfTheFields() {
        Map<String, List<String>> grouped = ValidationDrills.groupByField(List.of("name: is required", "price: bad", "name: too long: really"));
        assertEquals(List.of("name", "price"), List.copyOf(grouped.keySet()));
        assertEquals(List.of("is required", "too long: really"), grouped.get("name"));
        assertEquals(List.of("bad"), grouped.get("price"));
        assertTrue(ValidationDrills.groupByField(List.of()).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> ValidationDrills.groupByField(List.of("no field here")));
    }
}
