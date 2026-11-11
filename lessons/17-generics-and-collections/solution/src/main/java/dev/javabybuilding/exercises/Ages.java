package dev.javabybuilding.exercises;

import java.util.List;

/** Lesson 16, exercise 2: turning typed text into an age, and surviving bad text. */
public class Ages {

    /**
     * "25" gives 25 (spaces at the ends are ignored).
     *
     * @throws InvalidAgeException with the message "Age must be a number: " + text when the text is not a whole number
     *         (Integer.parseInt throws NumberFormatException: catch it and throw yours, passing it as the cause)
     * @throws InvalidAgeException with the message "Age must be from 0 to 120: " + age when the number is outside 0..120
     */
    public static int parseAge(String text) {
        int age;
        try {
            age = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidAgeException("Age must be a number: " + text, e);
        }
        if (age < 0 || age > 120) {
            throw new InvalidAgeException("Age must be from 0 to 120: " + age);
        }
        return age;
    }

    /** Like parseAge, but returns fallback instead of throwing. */
    public static int parseAgeOrDefault(String text, int fallback) {
        try {
            return parseAge(text);
        } catch (InvalidAgeException e) {
            return fallback;
        }
    }

    /** How many of the texts are NOT valid ages. */
    public static int countInvalid(List<String> texts) {
        int invalid = 0;
        for (String text : texts) {
            try {
                parseAge(text);
            } catch (InvalidAgeException e) {
                invalid++;
            }
        }
        return invalid;
    }

    /** The sum of the valid ages; invalid texts are skipped. */
    public static int sumValid(List<String> texts) {
        int sum = 0;
        for (String text : texts) {
            try {
                sum += parseAge(text);
            } catch (InvalidAgeException e) {
                // skip this one and carry on with the next text
            }
        }
        return sum;
    }
}
