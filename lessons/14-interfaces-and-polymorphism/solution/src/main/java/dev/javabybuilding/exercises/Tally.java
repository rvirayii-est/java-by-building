package dev.javabybuilding.exercises;

/** Lesson 09, exercise 2: a hand tally counter, like the one a guard uses to count customers walking in. */
public class Tally {

    public int count; // starts at 0 automatically: int fields default to 0

    /** Adds one. */
    public void increment() {
        count++;
    }

    /** Adds amount (it may be any whole number). */
    public void add(int amount) {
        count += amount;
    }

    /** Goes back to 0. */
    public void reset() {
        count = 0;
    }
}
