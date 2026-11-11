package dev.javabybuilding.exercises;

import java.util.List;

/**
 * Lesson 22, exercise: six small methods, each with ONE bug. They all compile and they all look reasonable.
 * The tests in Exercises22Test fail. For each failing test: read the message, reproduce it, find the line, fix the cause.
 * Do NOT change the tests, and do not rewrite the whole method: change the smallest thing that is wrong.
 */
public class BugHunt {

    /** The average of the values as a decimal number: {1, 2} gives 1.5. No values gives 0. */
    public static double average(int[] values) {
        if (values.length == 0) {
            return 0;
        }
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total / values.length;
    }

    /** The last item of the list. */
    public static String lastItem(List<String> items) {
        return items.get(items.size());
    }

    /** True when both words are spelled exactly the same. */
    public static boolean sameWord(String a, String b) {
        return a == b;
    }

    /** 1 + 2 + ... + n, so sumUpTo(4) is 10. */
    public static int sumUpTo(int n) {
        int sum = 0;
        for (int i = 1; i < n; i++) {
            sum += i;
        }
        return sum;
    }

    /** How much a discount takes off: percentOff(200, 20) is 40. */
    public static int percentOff(int price, int percent) {
        return percent / 100 * price;
    }

    /** How many of the letters a, e, i, o, u (either case) the text has. */
    public static int countVowels(String text) {
        int count = 0;
        for (int i = 1; i < text.length(); i++) {
            if ("aeiouAEIOU".indexOf(text.charAt(i)) >= 0) {
                count++;
            }
        }
        return count;
    }
}
