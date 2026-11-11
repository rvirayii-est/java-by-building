package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.javabybuilding.inventory.InvalidInputException;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BodyTest {

    @Test
    void readsTextWholeNumbersAndMoney() {
        Body body = new Body(Map.of("name", "Rice", "quantity", 3, "price", "98.50", "other", 2.5));
        assertEquals("Rice", body.text("name"));
        assertEquals(3, body.integer("quantity"));
        assertEquals(new BigDecimal("98.50"), body.decimal("price"));
        assertEquals(new BigDecimal("2.5"), body.decimal("other"));
    }

    @Test
    void missingOrBlankRequiredTextIsRefused() {
        Body body = new Body(Map.of("blank", "  "));
        assertEquals("Missing or empty field: name", assertThrows(InvalidInputException.class, () -> body.text("name")).getMessage());
        assertThrows(InvalidInputException.class, () -> body.text("blank"));
    }

    @Test
    void wrongKindsOfValueAreRefused() {
        Body body = new Body(Map.of("quantity", "3", "price", true));
        assertEquals("quantity must be a whole number", assertThrows(InvalidInputException.class, () -> body.integer("quantity")).getMessage());
        assertEquals("price must be a number", assertThrows(InvalidInputException.class, () -> body.decimal("price")).getMessage());
        assertEquals("missing must be a whole number", assertThrows(InvalidInputException.class, () -> body.integer("missing")).getMessage());
    }

    @Test
    void optionalTextMayBeLeftOutButNotBeANumber() {
        Body body = new Body(Map.of("n", 5));
        assertNull(body.optionalText("absent"));
        assertEquals("n must be text", assertThrows(InvalidInputException.class, () -> body.optionalText("n")).getMessage());
    }

    @Test
    void noBodyAtAllActsLikeAnEmptyOne() {
        assertThrows(InvalidInputException.class, () -> new Body(null).text("name"));
    }
}
