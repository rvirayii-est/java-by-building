package dev.javabybuilding.exercises;

import java.util.List;

/**
 * Lesson 22, exercise: six small methods, each with ONE bug in the version students receive. This is the FIXED version.
 * The comment above each method says what the bug was.
 */
public class BugHunt {

    /**
     * Bug was: `total / values.length` with two ints, which is integer division (5 / 2 is 2), so the answer lost its decimals.
     * Fix: do the division with a double.
     */
    public static double average(int[] values) {
        if (values.length == 0) {
            return 0;
        }
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return (double) total / values.length;
    }

    /**
     * Bug was: `items.get(items.size())`. Indexes run from 0 to size - 1, so the last item is at size - 1
     * (IndexOutOfBoundsException: Index 3 out of bounds for length 3).
     */
    public static String lastItem(List<String> items) {
        return items.get(items.size() - 1);
    }

    /** Bug was: `a == b`, which asks "are these the very same object?" instead of "do they have the same letters?". Fix: equals. */
    public static boolean sameWord(String a, String b) {
        return a.equals(b);
    }

    /** Bug was: the loop used `i < n`, so n itself was never added. Fix: `i <= n`. */
    public static int sumUpTo(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }

    /**
     * Bug was: `percent / 100 * price` with ints: 20 / 100 is 0, so the discount was always 0.
     * Fix: multiply first, divide last (price * percent / 100).
     */
    public static int percentOff(int price, int percent) {
        return price * percent / 100;
    }

    /** Bug was: the loop started at `i = 1`, so the first letter was never looked at. Fix: start at 0. */
    public static int countVowels(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if ("aeiouAEIOU".indexOf(text.charAt(i)) >= 0) {
                count++;
            }
        }
        return count;
    }
}
