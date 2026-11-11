package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Lesson 23, exercise 4: the checkpoint file my-checkpoint.txt, where you record the Git work you did. Its lines are
 * "key: value"; blank lines and lines starting with # are ignored. See my-checkpoint.example.txt.
 * <pre>
 * name: Ana Reyes
 * github: ana-reyes
 * branch: feature/add-reorder-report
 * commit: feat: show reorder suggestions in the report
 * pull-request: https://github.com/ana-reyes/java-by-building/pull/1
 * conflict-resolved: yes
 * </pre>
 */
public class Checkpoint {

    static final List<String> KEYS = List.of("name", "github", "branch", "commit", "pull-request", "conflict-resolved");

    /**
     * TODO: what is wrong with the file, as sentences, in this order. An empty list means the checkpoint is complete.
     *  1. If ConflictMarkers.hasConflict(lines): "The file still contains conflict markers (<<<<<<<, =======, >>>>>>>)."
     *  2. For each non-blank, non-# line (skip conflict marker lines) whose key (the text before the first colon) is not in KEYS:
     *     "Unknown line: " + the trimmed line
     *  3. For each key in KEYS, in order: if it is missing or empty: "Missing: " + key. Otherwise check the value and add
     *     key + ": " + a reason when it is wrong:
     *       github: letters, digits and single hyphens inside, 1 to 39 characters, not starting or ending with a hyphen
     *       branch: BranchNames.isValid
     *       commit: CommitMessages.problems is empty (use the first problem as the reason)
     *       pull-request: matches  https://github.com/<user>/<repo>/pull/<number>
     *       conflict-resolved: exactly "yes" ("must be yes once you have resolved a conflict")
     * Exercises23Test shows the exact sentences it expects. Do this one last.
     */
    public static List<String> validate(List<String> lines) {
        List<String> problems = new ArrayList<>();
        problems.add("TODO");
        return problems;
    }
}
