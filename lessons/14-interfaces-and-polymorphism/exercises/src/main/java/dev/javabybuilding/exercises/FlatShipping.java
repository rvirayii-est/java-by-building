package dev.javabybuilding.exercises;

/** One fixed fee, whatever the weight. */
public class FlatShipping implements Shipping {

    private final int flatFee;

    public FlatShipping(int flatFee) {
        this.flatFee = flatFee;
    }

    /** TODO: return the flat fee, whatever the weight. */
    @Override
    public int fee(int kilograms) {
        return -1;
    }

    /** TODO: return "Flat". */
    @Override
    public String label() {
        return null;
    }
}
