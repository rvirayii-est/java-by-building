package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class Exercises34Test {

    @Test
    void goodPathsNameThingsWithLowerCaseNouns() {
        assertTrue(RestDrills.isGoodResourcePath("/products"));
        assertTrue(RestDrills.isGoodResourcePath("/products/42"));
        assertTrue(RestDrills.isGoodResourcePath("/products/Rice%2025kg/stock"));
        assertTrue(RestDrills.isGoodResourcePath("/products/42/stock-movements"));
        assertTrue(RestDrills.isGoodResourcePath("/reports/sales"));
    }

    @Test
    void verbsCapitalsAndBadSlashesMakeAPathBad() {
        assertFalse(RestDrills.isGoodResourcePath("/getProducts"));
        assertFalse(RestDrills.isGoodResourcePath("/products/42/delete-it/7")); // position 2 is a name: it starts with a verb
        assertFalse(RestDrills.isGoodResourcePath("/createSale"));
        assertFalse(RestDrills.isGoodResourcePath("/Products"));
        assertFalse(RestDrills.isGoodResourcePath("/products/"));
        assertFalse(RestDrills.isGoodResourcePath("products"));
        assertFalse(RestDrills.isGoodResourcePath("/"));
        assertFalse(RestDrills.isGoodResourcePath("/products//stock"));
    }

    @Test
    void successStatusDependsOnTheMethod() {
        assertEquals(200, RestDrills.successStatus("GET"));
        assertEquals(201, RestDrills.successStatus("POST"));
        assertEquals(200, RestDrills.successStatus("PUT"));
        assertEquals(200, RestDrills.successStatus("PATCH"));
        assertEquals(204, RestDrills.successStatus("DELETE"));
    }

    @Test
    void successStatusRefusesOtherMethods() {
        assertThrows(IllegalArgumentException.class, () -> RestDrills.successStatus("FETCH"));
        assertThrows(IllegalArgumentException.class, () -> RestDrills.successStatus("get"));
    }

    @Test
    void problemsMapToStatuses() {
        assertEquals(400, RestDrills.statusFor(new IllegalArgumentException("bad")));
        assertEquals(400, RestDrills.statusFor(new NumberFormatException("x"))); // a kind of IllegalArgumentException
        assertEquals(404, RestDrills.statusFor(new NoSuchElementException("none")));
        assertEquals(409, RestDrills.statusFor(new IllegalStateException("clash")));
        assertEquals(500, RestDrills.statusFor(new RuntimeException("bug")));
        assertEquals(500, RestDrills.statusFor(new NullPointerException()));
    }

    @Test
    void idempotentMethodsCanBeRepeatedSafely() {
        for (String method : new String[] {"GET", "HEAD", "OPTIONS", "PUT", "DELETE"}) {
            assertTrue(RestDrills.isIdempotent(method), method);
        }
        assertFalse(RestDrills.isIdempotent("POST"));
        assertFalse(RestDrills.isIdempotent("PATCH"));
    }

    @Test
    void locationEncodesTheId() {
        assertEquals("/products/Rice%2025kg", RestDrills.locationFor("products", "Rice 25kg"));
        assertEquals("/sales/7", RestDrills.locationFor("sales", "7"));
        assertEquals("/products/A%2FB", RestDrills.locationFor("products", "A/B"));
    }
}
