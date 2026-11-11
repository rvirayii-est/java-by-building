package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises14Test {

    private final List<Shipping> options = List.of(new FlatShipping(5000), new PerKiloShipping(1500));

    @Test
    void flatShippingIgnoresTheWeight() {
        assertEquals(5000, new FlatShipping(5000).fee(1));
        assertEquals(5000, new FlatShipping(5000).fee(40));
    }

    @Test
    void perKiloShippingMultipliesByTheWeight() {
        assertEquals(4500, new PerKiloShipping(1500).fee(3));
    }

    @Test
    void eachOptionHasALabel() {
        assertEquals("Flat", new FlatShipping(1).label());
        assertEquals("Per kilo", new PerKiloShipping(1).label());
    }

    @Test
    void aSingleListHoldsBothKinds() {
        assertEquals(2, options.size());
        assertEquals(5000, options.get(0).fee(2));
        assertEquals(3000, options.get(1).fee(2));
    }

    @Test
    void cheapestFeeDependsOnTheWeight() {
        assertEquals(1500, Shippers.cheapestFee(options, 1));  // per kilo wins for a light parcel
        assertEquals(5000, Shippers.cheapestFee(options, 10)); // flat wins for a heavy one
    }

    @Test
    void cheapestLabelAndTheEmptyList() {
        assertEquals("Per kilo", Shippers.cheapestLabel(options, 1));
        assertEquals("Flat", Shippers.cheapestLabel(options, 10));
        assertEquals(-1, Shippers.cheapestFee(List.of(), 5));
        assertEquals("none", Shippers.cheapestLabel(List.of(), 5));
    }
}
