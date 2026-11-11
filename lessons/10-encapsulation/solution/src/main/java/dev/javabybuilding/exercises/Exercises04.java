package dev.javabybuilding.exercises;

public class Exercises04 {

    /** Position of the biggest number, or -1 for an empty array. highestQuantityIndex({3, 50, 10}) returns 1. */
    public static int highestQuantityIndex(int[] quantities) {
        if (quantities.length == 0) {
            return -1;
        }
        int best = 0;
        for (int i = 1; i < quantities.length; i++) {
            if (quantities[i] > quantities[best]) {
                best = i;
            }
        }
        return best;
    }

    /** How many times target appears. countEqual({5, 0, 5, 7}, 5) returns 2. */
    public static int countEqual(int[] values, int target) {
        int count = 0;
        for (int value : values) {
            if (value == target) {
                count++;
            }
        }
        return count;
    }

    /** A NEW array with the same numbers in the opposite order. The original is not changed. */
    public static int[] reversed(int[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[values.length - 1 - i] = values[i];
        }
        return result;
    }
}
