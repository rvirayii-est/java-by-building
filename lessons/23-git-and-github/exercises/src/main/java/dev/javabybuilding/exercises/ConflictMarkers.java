package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Lesson 23, exercise 3: what Git writes into a file when two branches changed the same lines.
 * <pre>
 * &lt;&lt;&lt;&lt;&lt;&lt;&lt; HEAD            start: OUR version (the branch you are on)
 * 4. Show stock report
 * =======                  separator
 * 4. Stock and reorder report
 * &gt;&gt;&gt;&gt;&gt;&gt;&gt; feature/x    end: THEIR version (the branch being merged in)
 * </pre>
 * Resolving a conflict means editing the file so none of the three marker lines is left, then git add + git commit.
 */
public class ConflictMarkers {

    public enum Side { OURS, THEIRS, BOTH }

    /**
     * TODO: true when any line is a conflict marker: it starts with seven less-than signs, OR it is exactly seven equals signs,
     * OR it starts with seven greater-than signs. (A line that only CONTAINS =======, like "a ======= b", is not a marker.)
     */
    public static boolean hasConflict(List<String> lines) {
        return false;
    }

    /**
     * TODO: the lines with every conflict settled by keeping one side or both (ours first, then theirs). Lines outside a
     * conflict are unchanged; the three marker lines are never kept.
     * Hint: keep a variable "state": 0 = outside, 1 = in our part, 2 = in their part, and change it at each marker.
     * Throw new IllegalArgumentException("A conflict was started but never closed.") if you end inside a conflict.
     */
    public static List<String> resolve(List<String> lines, Side keep) {
        return new ArrayList<>();
    }
}
