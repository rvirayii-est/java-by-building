package dev.javabybuilding.exercises;

/** Lesson 12, exercise 2: one thermometer. It remembers its own reading, so its methods are NOT static. */
public class Thermometer {

    private int celsius;

    /** TODO: remember the starting reading. */
    public Thermometer(int celsius) {
    }

    /** TODO: return the reading in Celsius. */
    public int getCelsius() {
        return -999;
    }

    /** TODO: the reading in Fahrenheit. Reuse the static helper: Temperature.toFahrenheit(celsius). */
    public int getFahrenheit() {
        return -999;
    }

    /** TODO: true when this thermometer reads freezing (reuse Temperature.isFreezing). */
    public boolean isFreezing() {
        return false;
    }

    /** TODO: raise this thermometer's reading by degrees (a negative number cools it). */
    public void warm(int degrees) {
    }
}
