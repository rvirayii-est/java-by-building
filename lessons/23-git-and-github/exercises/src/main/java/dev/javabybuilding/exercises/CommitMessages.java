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

    /**
     * TODO: every rule the message breaks, as a sentence each, in the order of the rules above. An empty list means fine.
     * The exact wording is checked only by the tests that look at a single reason: see Exercises23Test.
     * Hints: message.split("\n", -1) gives the lines; first.indexOf(": ") finds the colon; Character.isLowerCase(...).
     * The sentences the tests expect:
     *   "Start with one of " + TYPES + " followed by a colon and a space."
     *   "The summary after the colon must start with a small letter."
     *   "The first line must be at most 72 characters."
     *   "The first line must not end with a full stop."
     *   "Leave a blank line after the first line."
     */
    public static List<String> problems(String message) {
        List<String> problems = new ArrayList<>();
        problems.add("TODO");
        return problems;
    }

    public static boolean isValid(String message) {
        return problems(message).isEmpty();
    }
}
