package dev.javabybuilding.exercises;

/** Lesson 12, exercise 2: one thermometer. It remembers its own reading, so its methods are NOT static. */
public class Thermometer {

    private int celsius;

    public Thermometer(int celsius) {
        this.celsius = celsius;
    }

    public int getCelsius() {
        return celsius;
    }

    /** The reading in Fahrenheit. Reuse the static helper: Temperature.toFahrenheit(...). */
    public int getFahrenheit() {
        return Temperature.toFahrenheit(celsius);
    }

    /** True when this thermometer reads freezing (reuse Temperature.isFreezing). */
    public boolean isFreezing() {
        return Temperature.isFreezing(celsius);
    }

    /** Raises this thermometer's reading by degrees (use a negative number to cool it). */
    public void warm(int degrees) {
        celsius += degrees;
    }
}
