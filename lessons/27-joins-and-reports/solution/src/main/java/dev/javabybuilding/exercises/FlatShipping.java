package dev.javabybuilding.exercises;

/** One fixed fee, whatever the weight. */
public class FlatShipping implements Shipping {

    private final int flatFee;

    public FlatShipping(int flatFee) {
        this.flatFee = flatFee;
    }

    @Override
    public int fee(int kilograms) {
        return flatFee;
    }

    @Override
    public String label() {
        return "Flat";
    }
}
