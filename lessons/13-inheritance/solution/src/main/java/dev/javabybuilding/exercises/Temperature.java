package dev.javabybuilding.exercises;

/** Lesson 12, exercise 1: temperature helpers. Nothing here remembers anything, so everything is static. */
public class Temperature {

    /** Water freezes at 0 degrees Celsius. */
    public static final int FREEZING_CELSIUS = 0;

    /** Celsius to Fahrenheit with whole numbers: celsius x 9 / 5 + 32 (the division rounds toward zero). */
    public static int toFahrenheit(int celsius) {
        return celsius * 9 / 5 + 32;
    }

    /** True when celsius is at or below FREEZING_CELSIUS. */
    public static boolean isFreezing(int celsius) {
        return celsius <= FREEZING_CELSIUS;
    }
}
