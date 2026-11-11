package dev.javabybuilding.exercises;

public class Exercises05 {

    /** First letters of each word, in capitals. initials("juan dela cruz") returns "JDC". Empty text gives "". */
    public static String initials(String fullName) {
        StringBuilder result = new StringBuilder();
        for (String word : fullName.trim().split(" ")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return result.toString();
    }

    /** How many words? Words are separated by one or more spaces. countWords("  cooking  oil 1L ") returns 3. */
    public static int countWords(String text) {
        int count = 0;
        for (String word : text.trim().split(" ")) {
            if (!word.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /** Valid product code: exactly 3 capital letters, a dash, then 3 digits ("RIC-001"). */
    public static boolean isValidSku(String code) {
        if (code.length() != 7 || code.charAt(3) != '-') {
            return false;
        }
        for (int i = 0; i < 7; i++) {
            if (i < 3 && !Character.isUpperCase(code.charAt(i))) {
                return false;
            }
            if (i > 3 && !Character.isDigit(code.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
