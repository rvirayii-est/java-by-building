package dev.javabybuilding.exercises;

/** Lesson 14, exercise 1: a promise that every shipping option can say what it costs for a weight. (Done for you.) */
public interface Shipping {

    /** The fee in centavos for a parcel of this many kilograms. */
    int fee(int kilograms);

    /** A short name for receipts. */
    String label();
}
