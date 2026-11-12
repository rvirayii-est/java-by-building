package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Lesson 48 drills: check your OWN portfolio before an employer does. Small, honest tools: a README checker, a resume-bullet
 * checker, a demo timer and a scoring rule. Complete each TODO until Exercises48Test is green.
 */
public class PortfolioDrills {

    /** The ten headings (each written as "## Heading") that the portfolio README must have, in this order. */
    public static final List<String> REQUIRED_SECTIONS = List.of("What it is", "Features", "Tech stack", "Run it", "API", "Tests", "Architecture",
            "Demo", "What I learned", "What I would do next");

    /**
     * Which required sections are missing from a README? A section is present when a line is exactly "## " + the heading (capitals do not
     * matter; trailing spaces do not matter). Returns the missing ones in the order of REQUIRED_SECTIONS.
     */
    public static List<String> missingSections(String readme) {
        Set<String> found = new java.util.HashSet<>();
        for (String line : readme.lines().toList()) {
            if (line.startsWith("## ")) {
                found.add(line.substring(3).strip().toLowerCase(Locale.ROOT));
            }
        }
        return REQUIRED_SECTIONS.stream().filter(section -> !found.contains(section.toLowerCase(Locale.ROOT))).toList();
    }

    private static final Set<String> VERBS = Set.of("built", "designed", "implemented", "added", "wrote", "created", "automated", "secured",
            "documented", "containerised", "containerized", "deployed", "tested", "reduced", "improved", "fixed", "migrated", "developed", "configured");

    /**
     * What is weak about a resume bullet? Returns these messages, in this order (empty list = a good bullet):
     * "start with an action verb" (the first word, any capitals, is not in VERBS),
     * "include a number" (no digit anywhere: numbers make a claim checkable: tests, endpoints, minutes),
     * "keep it to one line of 160 characters or fewer",
     * "do not say 'responsible for'" (it says what you were given, not what you did; any capitals).
     */
    public static List<String> bulletProblems(String bullet) {
        List<String> problems = new ArrayList<>();
        String text = bullet.strip();
        String first = text.isEmpty() ? "" : text.split("\\s+")[0].toLowerCase(Locale.ROOT);
        if (!VERBS.contains(first)) {
            problems.add("start with an action verb");
        }
        if (!Pattern.compile("\\d").matcher(text).find()) {
            problems.add("include a number");
        }
        if (text.length() > 160 || text.contains("\n")) {
            problems.add("keep it to one line of 160 characters or fewer");
        }
        if (text.toLowerCase(Locale.ROOT).contains("responsible for")) {
            problems.add("do not say 'responsible for'");
        }
        return problems;
    }

    /** Seconds as minutes and seconds: 295 is "4:55", 300 is "5:00", 61 is "1:01", 0 is "0:00". */
    public static String timeLabel(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    /**
     * The assessment decision for a list of criterion scores, each 0 (no evidence), 1 (partly), 2 (meets), 3 (exceeds): "Competent" when
     * EVERY score is at least 2, otherwise "Not yet competent". One weak criterion cannot be hidden by strong ones: that is how
     * competency-based assessment differs from an average. Throws IllegalArgumentException for an empty list or a score outside 0..3.
     */
    public static String decision(List<Integer> scores) {
        checkScores(scores);
        return scores.stream().allMatch(s -> s >= 2) ? "Competent" : "Not yet competent";
    }

    /** The scores as a percentage of the maximum (3 per criterion), rounded to a whole number: [2,2,2,2,2] is 67, [3,3,3,3,1] is 87. Same checks as decision. */
    public static int percent(List<Integer> scores) {
        checkScores(scores);
        return (int) Math.round(scores.stream().mapToInt(Integer::intValue).sum() * 100.0 / (3 * scores.size()));
    }

    private static void checkScores(List<Integer> scores) {
        if (scores.isEmpty() || scores.stream().anyMatch(s -> s < 0 || s > 3)) {
            throw new IllegalArgumentException("Need at least one score, each between 0 and 3");
        }
    }
}
