package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Lesson 23, exercise 4: the checkpoint file my-checkpoint.txt, where you record the Git work you did. Its lines are
 * "key: value"; blank lines and lines starting with # are ignored.
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

    private static final Pattern GITHUB_NAME = Pattern.compile("^[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?$");
    private static final Pattern PULL_REQUEST = Pattern.compile("^https://github\\.com/[A-Za-z0-9-]+/[A-Za-z0-9._-]+/pull/[0-9]+$");

    /** What is wrong with the file, in the order of KEYS. An empty list means the checkpoint is complete. */
    public static List<String> validate(List<String> lines) {
        List<String> problems = new ArrayList<>();
        if (ConflictMarkers.hasConflict(lines)) {
            problems.add("The file still contains conflict markers (<<<<<<<, =======, >>>>>>>).");
        }
        java.util.Map<String, String> values = new java.util.LinkedHashMap<>();
        for (String line : lines) {
            String text = line.trim();
            if (text.isEmpty() || text.startsWith("#") || ConflictMarkers.hasConflict(List.of(text))) {
                continue;
            }
            int colon = text.indexOf(':');
            String key = colon < 0 ? text : text.substring(0, colon).trim();
            if (!KEYS.contains(key)) {
                problems.add("Unknown line: " + text);
            } else {
                values.put(key, text.substring(colon + 1).trim());
            }
        }
        for (String key : KEYS) {
            String value = values.get(key);
            if (value == null || value.isEmpty()) {
                problems.add("Missing: " + key);
            } else {
                String problem = check(key, value);
                if (problem != null) {
                    problems.add(key + ": " + problem);
                }
            }
        }
        return problems;
    }

    /** The problem with one value, or null when it is fine. */
    private static String check(String key, String value) {
        switch (key) {
            case "github":
                return GITHUB_NAME.matcher(value).matches() ? null : "not a GitHub user name";
            case "branch":
                return BranchNames.isValid(value) ? null : "use kind/words-with-hyphens, for example feature/add-reorder-report";
            case "commit":
                List<String> commitProblems = CommitMessages.problems(value);
                return commitProblems.isEmpty() ? null : commitProblems.get(0);
            case "pull-request":
                return PULL_REQUEST.matcher(value).matches() ? null : "use the full address, https://github.com/<user>/<repo>/pull/<number>";
            case "conflict-resolved":
                return value.equals("yes") ? null : "must be yes once you have resolved a conflict";
            default:
                return null; // name: anything non-empty is fine
        }
    }
}
