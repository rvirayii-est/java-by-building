package dev.javabybuilding.exercises;

/** A fee for every kilogram. */
public class PerKiloShipping implements Shipping {

    private final int ratePerKilo;

    public PerKiloShipping(int ratePerKilo) {
        this.ratePerKilo = ratePerKilo;
    }

    /** TODO: the rate multiplied by the number of kilograms. */
    @Override
    public int fee(int kilograms) {
        return -1;
    }

    /** TODO: return "Per kilo". */
    @Override
    public String label() {
        return null;
    }
}
