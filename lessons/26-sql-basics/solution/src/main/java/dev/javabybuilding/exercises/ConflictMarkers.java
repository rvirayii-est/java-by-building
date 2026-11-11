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

    /** True when any line is a conflict marker (starts with seven &lt;, = or &gt; characters). */
    public static boolean hasConflict(List<String> lines) {
        for (String line : lines) {
            if (line.startsWith("<<<<<<<") || line.equals("=======") || line.startsWith(">>>>>>>")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The lines with every conflict settled by keeping one side or both (ours first, then theirs). Lines outside a conflict
     * are unchanged.
     *
     * @throws IllegalArgumentException when a conflict is not closed properly (a start without a separator and an end)
     */
    public static List<String> resolve(List<String> lines, Side keep) {
        List<String> result = new ArrayList<>();
        int state = 0; // 0 = outside a conflict, 1 = in our part, 2 = in their part
        for (String line : lines) {
            if (line.startsWith("<<<<<<<") && state == 0) {
                state = 1;
            } else if (line.equals("=======") && state == 1) {
                state = 2;
            } else if (line.startsWith(">>>>>>>") && state == 2) {
                state = 0;
            } else if (state == 0) {
                result.add(line);
            } else if (state == 1 && keep != Side.THEIRS) {
                result.add(line);
            } else if (state == 2 && keep != Side.OURS) {
                result.add(line);
            }
        }
        if (state != 0) {
            throw new IllegalArgumentException("A conflict was started but never closed.");
        }
        return result;
    }
}
