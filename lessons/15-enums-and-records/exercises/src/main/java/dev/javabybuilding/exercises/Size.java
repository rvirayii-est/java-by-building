package dev.javabybuilding.exercises;

/** Lesson 15, exercise 1: drink sizes. Each constant carries its own extra price (centavos). */
public enum Size {
    SMALL(0),
    MEDIUM(500),
    LARGE(1000);

    private final int extraCentavos;

    Size(int extraCentavos) {
        this.extraCentavos = extraCentavos;
    }

    /** TODO: return the extra price of this size. */
    public int extraCentavos() {
        return -1;
    }

    /** TODO: the base price plus this size's extra. */
    public int priceFor(int baseCentavos) {
        return -1;
    }

    /** TODO: true when this size comes after other in the list above. Hint: compareTo compares positions (ordinal). */
    public boolean isLargerThan(Size other) {
        return false;
    }
}
