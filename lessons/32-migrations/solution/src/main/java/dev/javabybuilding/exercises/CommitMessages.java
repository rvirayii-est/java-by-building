package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Lesson 23, exercise 1: this course writes commit messages in the form  type: short summary  (for example
 * "feat: add reorder suggestions to the report"). This class checks a message the way a reviewer would.
 *
 * The rules:
 * 1. It starts with one of the types in TYPES, then a colon and ONE space.
 * 2. The summary after "type: " is not empty and starts with a small (lower-case) letter.
 * 3. The first line is at most 72 characters long.
 * 4. The first line does not end with a full stop.
 * 5. If there is more than one line, the second line is empty (a blank line separates the summary from the details).
 */
public class CommitMessages {

    public static final List<String> TYPES = List.of("feat", "fix", "docs", "test", "refactor", "chore");

    /** Every rule the message breaks, in the order of the list above. An empty list means the message is fine. */
    public static List<String> problems(String message) {
        List<String> problems = new ArrayList<>();
        String[] lines = message.split("\n", -1);
        String first = lines[0];

        int colon = first.indexOf(": ");
        String type = colon < 0 ? "" : first.substring(0, colon);
        String summary = colon < 0 ? "" : first.substring(colon + 2);

        if (!TYPES.contains(type)) {
            problems.add("Start with one of " + TYPES + " followed by a colon and a space.");
        }
        if (summary.isEmpty() || !Character.isLowerCase(summary.charAt(0))) {
            problems.add("The summary after the colon must start with a small letter.");
        }
        if (first.length() > 72) {
            problems.add("The first line must be at most 72 characters.");
        }
        if (first.endsWith(".")) {
            problems.add("The first line must not end with a full stop.");
        }
        if (lines.length > 1 && !lines[1].isEmpty()) {
            problems.add("Leave a blank line after the first line.");
        }
        return problems;
    }

    public static boolean isValid(String message) {
        return problems(message).isEmpty();
    }
}
