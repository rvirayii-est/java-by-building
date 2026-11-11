package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises09Test {

    @Test
    void rectangleAreaAndPerimeter() {
        Rectangle label = new Rectangle(6, 4);
        assertEquals(24, label.area());
        assertEquals(20, label.perimeter());
    }

    @Test
    void rectangleKnowsWhenItIsASquare() {
        assertTrue(new Rectangle(5, 5).isSquare());
        assertFalse(new Rectangle(5, 6).isSquare());
    }

    @Test
    void changingAFieldChangesTheAnswers() {
        Rectangle label = new Rectangle(2, 3);
        label.width = 10;
        assertEquals(30, label.area());
    }

    @Test
    void aNewTallyIsZero() {
        assertEquals(0, new Tally().count);
    }

    @Test
    void tallyCountsUp() {
        Tally tally = new Tally();
        tally.increment();
        tally.increment();
        tally.add(5);
        assertEquals(7, tally.count);
    }

    @Test
    void resetGoesBackToZeroAndTalliesAreIndependent() {
        Tally morning = new Tally();
        Tally evening = new Tally();
        morning.add(9);
        evening.add(4);
        morning.reset();
        assertEquals(0, morning.count);
        assertEquals(4, evening.count);
    }
}
