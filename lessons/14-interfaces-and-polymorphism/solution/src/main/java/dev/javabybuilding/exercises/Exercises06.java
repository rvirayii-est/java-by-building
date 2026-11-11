package dev.javabybuilding.exercises;

public class Exercises06 {

    /** True when the answer means yes: "y" or "yes", any capitals, spaces at the ends ignored. Everything else is false. */
    public static boolean isYes(String answer) {
        String a = answer.trim();
        return a.equalsIgnoreCase("y") || a.equalsIgnoreCase("yes");
    }

    private static boolean isWholeNumber(String text) {
        if (text.isEmpty() || text.length() > 9) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Typed text as a quantity from 1 to max, or -1 when it is not a whole number or is outside 1..max. */
    public static int parseQuantity(String text, int max) {
        String t = text.trim();
        if (!isWholeNumber(t)) {
            return -1;
        }
        int number = Integer.parseInt(t);
        if (number < 1 || number > max) {
            return -1;
        }
        return number;
    }

    /** Adds up the whole numbers in a line, ignoring words. sumOfNumbers("10 20 abc 5") returns 35. */
    public static int sumOfNumbers(String line) {
        int sum = 0;
        for (String word : line.split(" ")) {
            if (isWholeNumber(word)) {
                sum += Integer.parseInt(word);
            }
        }
        return sum;
    }
}
