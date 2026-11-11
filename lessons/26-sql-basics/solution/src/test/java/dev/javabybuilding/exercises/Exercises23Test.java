package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Exercises23Test {

    // ---- commit messages
    @ParameterizedTest
    @ValueSource(strings = {
        "feat: add reorder suggestions to the report",
        "fix: correct the sign of a sale total",
        "docs: explain how to resolve a conflict",
        "refactor: split Menu.run into small methods",
        "feat: add search\n\nThe search ignores capitals and spaces."
    })
    void goodCommitMessagesAreAccepted(String message) {
        assertTrue(CommitMessages.isValid(message), CommitMessages.problems(message).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "added stuff",                       // no type
        "feature: add search",               // unknown type
        "feat:add search",                   // no space after the colon
        "feat: Add search",                  // capital letter
        "feat: add search.",                 // full stop
        "feat: ",                            // empty summary
        "feat: add search\nit also fixes x"  // no blank line after the first line
    })
    void badCommitMessagesAreRefused(String message) {
        assertFalse(CommitMessages.isValid(message));
    }

    @Test
    void aTooLongFirstLineIsReportedWithThatReason() {
        String message = "feat: " + "a".repeat(70);
        assertEquals(List.of("The first line must be at most 72 characters."), CommitMessages.problems(message));
    }

    @Test
    void aMessageCanBreakSeveralRulesAtOnce() {
        List<String> problems = CommitMessages.problems("Added stuff.");
        assertEquals(3, problems.size()); // no type, empty summary (there is no colon), full stop
    }

    // ---- branch names
    @Test
    void aTitleBecomesAFeatureBranch() {
        assertEquals("feature/add-reorder-report", BranchNames.fromTitle("Add reorder report!"));
        assertEquals("feature/fix-the-1st-bug", BranchNames.fromTitle("  Fix the 1st   bug  "));
        assertEquals("fix/sale-total-sign", BranchNames.fromTitle("fix", "Sale total: sign"));
    }

    @Test
    void aTitleWithoutLettersOrADifferentKindIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> BranchNames.fromTitle("!!!"));
        assertThrows(IllegalArgumentException.class, () -> BranchNames.fromTitle("hotfix", "something"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"feature/add-report", "fix/x", "docs/setup-2", "chore/update-junit"})
    void goodBranchNames(String name) {
        assertTrue(BranchNames.isValid(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"main", "feature/", "feature/Add-Report", "feature/add_report", "feature/add--report", "feature/-add", "my-branch", "feature/add report"})
    void badBranchNames(String name) {
        assertFalse(BranchNames.isValid(name));
    }

    // ---- conflict markers
    private final List<String> conflicted = List.of(
            "1. Add product",
            "<<<<<<< HEAD",
            "4. Show stock report",
            "=======",
            "4. Stock and reorder report",
            ">>>>>>> feature/reorder-report",
            "5. Quit");

    @Test
    void aFileWithMarkersHasAConflictAndACleanFileDoesNot() {
        assertTrue(ConflictMarkers.hasConflict(conflicted));
        assertFalse(ConflictMarkers.hasConflict(List.of("1. Add product", "a line with ======= inside", "5. Quit")));
    }

    @Test
    void resolveKeepsOurSideTheirSideOrBoth() {
        assertEquals(List.of("1. Add product", "4. Show stock report", "5. Quit"), ConflictMarkers.resolve(conflicted, ConflictMarkers.Side.OURS));
        assertEquals(List.of("1. Add product", "4. Stock and reorder report", "5. Quit"), ConflictMarkers.resolve(conflicted, ConflictMarkers.Side.THEIRS));
        assertEquals(List.of("1. Add product", "4. Show stock report", "4. Stock and reorder report", "5. Quit"),
                ConflictMarkers.resolve(conflicted, ConflictMarkers.Side.BOTH));
    }

    @Test
    void resolveHandlesTwoConflictsAndNoConflicts() {
        List<String> two = List.of("a", "<<<<<<< HEAD", "b1", "=======", "b2", ">>>>>>> x", "c", "<<<<<<< HEAD", "d1", "=======", "d2", ">>>>>>> x");
        assertEquals(List.of("a", "b2", "c", "d2"), ConflictMarkers.resolve(two, ConflictMarkers.Side.THEIRS));
        assertEquals(List.of("a", "b"), ConflictMarkers.resolve(List.of("a", "b"), ConflictMarkers.Side.BOTH));
    }

    @Test
    void aConflictThatIsNeverClosedIsAnError() {
        assertThrows(IllegalArgumentException.class,
                () -> ConflictMarkers.resolve(List.of("<<<<<<< HEAD", "a", "=======", "b"), ConflictMarkers.Side.BOTH));
    }

    // ---- the checkpoint file
    private final List<String> goodCheckpoint = List.of(
            "# my checkpoint",
            "name: Ana Reyes",
            "github: ana-reyes",
            "branch: feature/add-reorder-report",
            "commit: feat: show reorder suggestions in the report",
            "pull-request: https://github.com/ana-reyes/java-by-building/pull/1",
            "conflict-resolved: yes");

    @Test
    void aCompleteCheckpointHasNoProblems() {
        assertEquals(List.of(), Checkpoint.validate(goodCheckpoint));
    }

    @Test
    void missingAndWrongValuesAreNamed() {
        List<String> bad = List.of(
                "name: Ana Reyes",
                "github: ana reyes",
                "branch: my-branch",
                "commit: Added stuff.",
                "pull-request: github.com/ana/repo/pull/1",
                "conflict-resolved: no");
        List<String> problems = Checkpoint.validate(bad);
        assertEquals(5, problems.size());
        assertTrue(problems.get(0).startsWith("github: "));
        assertTrue(problems.get(1).startsWith("branch: "));
        assertTrue(problems.get(2).startsWith("commit: "));
        assertTrue(problems.get(3).startsWith("pull-request: "));
        assertEquals("conflict-resolved: must be yes once you have resolved a conflict", problems.get(4));
    }

    @Test
    void anEmptyFileIsMissingEverything() {
        assertEquals(List.of("Missing: name", "Missing: github", "Missing: branch", "Missing: commit", "Missing: pull-request",
                "Missing: conflict-resolved"), Checkpoint.validate(List.of()));
    }

    @Test
    void leftOverConflictMarkersAndUnknownLinesAreReported() {
        List<String> lines = new java.util.ArrayList<>(goodCheckpoint);
        lines.add("<<<<<<< HEAD");
        lines.add("colour: blue");
        List<String> problems = Checkpoint.validate(lines);
        assertEquals(2, problems.size());
        assertTrue(problems.get(0).startsWith("The file still contains conflict markers"));
        assertEquals("Unknown line: colour: blue", problems.get(1));
    }
}
