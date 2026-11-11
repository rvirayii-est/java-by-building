package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises15Test {

    @Test
    void eachSizeHasItsExtraPrice() {
        assertEquals(0, Size.SMALL.extraCentavos());
        assertEquals(500, Size.MEDIUM.extraCentavos());
        assertEquals(1000, Size.LARGE.extraCentavos());
    }

    @Test
    void priceForAddsTheExtra() {
        assertEquals(6500, Size.MEDIUM.priceFor(6000));
        assertEquals(6000, Size.SMALL.priceFor(6000));
    }

    @Test
    void sizesAreOrderedSmallToLarge() {
        assertTrue(Size.LARGE.isLargerThan(Size.MEDIUM));
        assertFalse(Size.SMALL.isLargerThan(Size.MEDIUM));
        assertFalse(Size.MEDIUM.isLargerThan(Size.MEDIUM));
    }

    @Test
    void coordinatesWithTheSameValuesAreEqual() {
        assertEquals(new Coordinate(2, 3), new Coordinate(2, 3));
        assertEquals("Coordinate[x=2, y=3]", new Coordinate(2, 3).toString());
        assertEquals(2, new Coordinate(2, 3).x());
    }

    @Test
    void walkingDistanceAddsTheTwoDifferences() {
        assertEquals(7, new Coordinate(1, 1).walkingDistanceTo(new Coordinate(4, 5)));
        assertEquals(7, new Coordinate(4, 5).walkingDistanceTo(new Coordinate(1, 1)));
    }

    @Test
    void movedGivesANewCoordinateAndLeavesTheOldOneAlone() {
        Coordinate start = new Coordinate(1, 1);
        Coordinate end = start.moved(2, -1);
        assertEquals(new Coordinate(3, 0), end);
        assertEquals(new Coordinate(1, 1), start);
    }
}
