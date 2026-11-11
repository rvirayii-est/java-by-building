package dev.javabybuilding.exercises;

/** A fee for every kilogram. */
public class PerKiloShipping implements Shipping {

    private final int ratePerKilo;

    public PerKiloShipping(int ratePerKilo) {
        this.ratePerKilo = ratePerKilo;
    }

    @Override
    public int fee(int kilograms) {
        return ratePerKilo * kilograms;
    }

    @Override
    public String label() {
        return "Per kilo";
    }
}
