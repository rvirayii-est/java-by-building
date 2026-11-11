package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * These tests check the FILES you produce while practising Git: the resolved practice file and your checkpoint.
 * They read files from the module folder (surefire sets "basedir"); they never write anything.
 */
class YourGitWorkTest {

    private static Path inModule(String name) {
        return Path.of(System.getProperty("basedir", "."), name);
    }

    private static List<String> read(String name) throws IOException {
        Path file = inModule(name);
        assertTrue(Files.exists(file), "Missing file: " + file.getFileName() + " (see the lesson, exercise steps)");
        return Files.readAllLines(file);
    }

    @Test
    void thePracticeFileIsFreeOfConflictMarkers() throws IOException {
        List<String> lines = read("practice/menu-notes.txt");

        // Checked here directly (not with ConflictMarkers) so this test does not depend on your other exercises.
        boolean hasMarker = lines.stream().anyMatch(line -> line.startsWith("<<<<<<<") || line.equals("=======") || line.startsWith(">>>>>>>"));
        assertFalse(hasMarker, "practice/menu-notes.txt still has <<<<<<<, ======= or >>>>>>> lines");
    }

    @Test
    void thePracticeFileKeepsBothChangesOnLineFour() throws IOException {
        List<String> lines = read("practice/menu-notes.txt");

        assertEquals(6, lines.size(), "expected the title and five menu lines");
        assertEquals("Shop menu notes", lines.get(0));
        assertEquals("1. Add product", lines.get(1));
        assertEquals("5. Quit", lines.get(5));
        String four = lines.get(4);
        assertTrue(four.startsWith("4. "), "line 5 of the file must be the choice 4");
        assertTrue(four.contains("Show") && four.toLowerCase().contains("reorder"),
                "choice 4 must keep BOTH ideas: the word 'Show' (from main) and 'reorder' (from your branch)");
    }

    @Test
    void yourCheckpointIsCompleteAndCorrect() throws IOException {
        List<String> problems = Checkpoint.validate(read("my-checkpoint.txt"));

        assertEquals(List.of(), problems);
    }
}
