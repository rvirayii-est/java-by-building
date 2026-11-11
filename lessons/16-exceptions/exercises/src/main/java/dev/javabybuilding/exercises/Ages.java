package dev.javabybuilding.exercises;

import java.util.List;

/** Lesson 16, exercise 2: turning typed text into an age, and surviving bad text. */
public class Ages {

    /**
     * TODO: "25" gives 25 (spaces at the ends are ignored). Use Integer.parseInt(text.trim()).
     * - If the text is not a whole number, parseInt throws NumberFormatException. Catch it and throw
     *   new InvalidAgeException("Age must be a number: " + text, e) (passing e keeps the original problem as the cause).
     * - If the number is below 0 or above 120, throw new InvalidAgeException("Age must be from 0 to 120: " + age).
     */
    public static int parseAge(String text) {
        return -1;
    }

    /** TODO: like parseAge, but returns fallback instead of throwing. Use try { ... } catch (InvalidAgeException e) { ... }. */
    public static int parseAgeOrDefault(String text, int fallback) {
        return 0;
    }

    /** TODO: how many of the texts are NOT valid ages. Call parseAge inside a try block and count each catch. */
    public static int countInvalid(List<String> texts) {
        return -1;
    }

    /** TODO: the sum of the valid ages; invalid texts are skipped (catch the exception and carry on with the next text). */
    public static int sumValid(List<String> texts) {
        return -1;
    }
}
