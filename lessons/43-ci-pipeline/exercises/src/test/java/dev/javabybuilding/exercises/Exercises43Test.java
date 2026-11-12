package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.exercises.PipelineDrills.Action;
import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises43Test {

    @Test
    void theBadgeLinksToTheWorkflowRuns() {
        assertEquals("[![CI](https://github.com/ana/shop/actions/workflows/ci.yml/badge.svg)](https://github.com/ana/shop/actions/workflows/ci.yml)",
                PipelineDrills.badgeMarkdown("ana", "shop", "ci.yml"));
    }

    @Test
    void parseUsesSplitsOwnerNameAndRef() {
        assertEquals(new Action("actions", "setup-java", "v4"), PipelineDrills.parseUses("actions/setup-java@v4"));
        assertEquals(new Action("my-org", "my.action", "v1.2.3"), PipelineDrills.parseUses("my-org/my.action@v1.2.3"));
    }

    @Test
    void parseUsesRefusesWhatIsNotAnActionReference() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> PipelineDrills.parseUses("actions/checkout"));
        assertEquals("Not an action reference: actions/checkout", e.getMessage());
        assertThrows(IllegalArgumentException.class, () -> PipelineDrills.parseUses(null));
    }

    @Test
    void versionsAndCommitIdsArePinnedBranchesAreNot() {
        assertTrue(PipelineDrills.isPinned("actions/checkout@v4"));
        assertTrue(PipelineDrills.isPinned("actions/checkout@v4.1.2"));
        assertTrue(PipelineDrills.isPinned("actions/checkout@" + "a".repeat(40)));
        assertFalse(PipelineDrills.isPinned("actions/checkout@main"));
        assertFalse(PipelineDrills.isPinned("actions/checkout@master"));
        assertFalse(PipelineDrills.isPinned("actions/checkout"));
        assertFalse(PipelineDrills.isPinned(null));
    }

    @Test
    void aPipelineStopsAtTheFirstFailingStep() {
        List<String> steps = List.of("checkout", "build", "test", "package");
        assertEquals(List.of("checkout", "build"), PipelineDrills.stepsThatRun(steps, 1));
        assertEquals(steps, PipelineDrills.stepsThatRun(steps, -1));
        assertEquals(List.of("checkout"), PipelineDrills.stepsThatRun(steps, 0));
    }

    @Test
    void theCacheKeyChangesWhenThePomChanges() {
        String key = PipelineDrills.cacheKey("linux", "<project>a</project>");
        assertTrue(key.matches("maven-linux-[0-9a-f]{8}"), key);
        assertEquals(key, PipelineDrills.cacheKey("linux", "<project>a</project>"));
        assertNotEquals(key, PipelineDrills.cacheKey("linux", "<project>b</project>"));
        assertNotEquals(key, PipelineDrills.cacheKey("windows", "<project>a</project>"));
    }
}
