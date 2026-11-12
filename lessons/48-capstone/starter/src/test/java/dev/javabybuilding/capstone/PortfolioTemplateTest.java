package dev.javabybuilding.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** The files a student copies for the capstone are tested too: a template with a missing section, or a 5-minute script that adds up to 6, would teach the wrong thing. */
class PortfolioTemplateTest {

    private static final Path TEMPLATE = Path.of("portfolio", "README.template.md");
    private static final Path SCRIPT = Path.of("portfolio", "DEMO_SCRIPT.md");
    /** The same ten headings as PortfolioDrills.REQUIRED_SECTIONS (the drill is for the student; this test must not depend on it). */
    private static final List<String> SECTIONS = List.of("What it is", "Features", "Tech stack", "Run it", "API", "Tests", "Architecture", "Demo",
            "What I learned", "What I would do next");

    /** The template with Unix line endings: a Windows checkout of the repository may have CRLF. */
    private static String template() throws IOException {
        return Files.readString(TEMPLATE).replace("\r\n", "\n");
    }

    @Test
    void theTemplateHasAllTenSectionsInOrder() throws IOException {
        String template = template();
        assertEquals(List.of(), SECTIONS.stream().filter(section -> !template.contains("\n## " + section + "\n")).toList(), "missing sections");
        int last = -1;
        for (String section : SECTIONS) {
            int at = template.indexOf("\n## " + section);
            assertTrue(at > last, section + " is out of order");
            last = at;
        }
    }

    @Test
    void theTemplateHasTheCiBadgeAndTheRunCommands() throws IOException {
        String template = template();
        assertTrue(template.contains("/actions/workflows/ci.yml/badge.svg"));
        assertTrue(template.contains("./mvnw -B verify"));
        assertTrue(template.contains("/swagger-ui.html"));
    }

    @Test
    void theTemplateHoldsNoRealSecretsOnlyTheLearningLogins() throws IOException {
        String template = Files.readString(TEMPLATE).toLowerCase();
        assertFalse(template.contains("ghp_"));
        assertTrue(template.contains("for learning only"), "the demo passwords must be labelled");
    }

    @Test
    void theDemoScriptAddsUpToExactlyFiveMinutesWithNoGaps() throws IOException {
        Matcher m = Pattern.compile("(?m)^### (\\d):(\\d\\d) - (\\d):(\\d\\d) ").matcher(Files.readString(SCRIPT));
        int expectedStart = 0;
        int sections = 0;
        while (m.find()) {
            int start = Integer.parseInt(m.group(1)) * 60 + Integer.parseInt(m.group(2));
            int end = Integer.parseInt(m.group(3)) * 60 + Integer.parseInt(m.group(4));
            assertEquals(expectedStart, start, "a gap or an overlap before " + m.group());
            assertTrue(end > start, m.group());
            expectedStart = end;
            sections++;
        }
        assertEquals(300, expectedStart, "the script must end at 5:00 (300 seconds) but ends at " + expectedStart + " seconds");
        assertTrue(sections >= 6, "sections found: " + sections);
    }
}
