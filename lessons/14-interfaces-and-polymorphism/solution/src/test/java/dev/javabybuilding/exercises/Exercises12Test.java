package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises12Test {

    @Test
    void celsiusToFahrenheit() {
        assertEquals(32, Temperature.toFahrenheit(0));
        assertEquals(212, Temperature.toFahrenheit(100));
        assertEquals(-40, Temperature.toFahrenheit(-40));
    }

    @Test
    void freezingIsZeroOrBelow() {
        assertEquals(0, Temperature.FREEZING_CELSIUS);
        assertTrue(Temperature.isFreezing(0));
        assertTrue(Temperature.isFreezing(-5));
        assertFalse(Temperature.isFreezing(1));
    }

    @Test
    void aThermometerRemembersItsReading() {
        Thermometer kitchen = new Thermometer(30);
        assertEquals(30, kitchen.getCelsius());
        assertEquals(86, kitchen.getFahrenheit());
    }

    @Test
    void aThermometerKnowsWhenItIsFreezing() {
        assertTrue(new Thermometer(-2).isFreezing());
        assertFalse(new Thermometer(25).isFreezing());
    }

    @Test
    void warmChangesOnlyThatThermometer() {
        Thermometer freezer = new Thermometer(-18);
        Thermometer room = new Thermometer(25);
        freezer.warm(20);
        assertEquals(2, freezer.getCelsius());
        assertEquals(25, room.getCelsius());
    }

    @Test
    void warmCanMakeAThermometerStopFreezing() {
        Thermometer t = new Thermometer(-1);
        assertTrue(t.isFreezing());
        t.warm(5);
        assertFalse(t.isFreezing());
    }
}
