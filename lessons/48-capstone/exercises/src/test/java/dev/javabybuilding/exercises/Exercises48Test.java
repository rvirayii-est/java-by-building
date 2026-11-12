package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises48Test {

    @Test
    void aCompleteReadmeHasNothingMissing() {
        String readme = "# Shop\n" + PortfolioDrills.REQUIRED_SECTIONS.stream().map(s -> "## " + s + "\ntext\n").reduce("", String::concat);
        assertEquals(List.of(), PortfolioDrills.missingSections(readme));
    }

    @Test
    void missingSectionsAreListedInTheRequiredOrderAndCapitalsDoNotMatter() {
        String readme = "# Shop\n## what it is\nx\n## Features   \nx\n## RUN IT\nx\n### Demo\nx\n";
        assertEquals(List.of("Tech stack", "API", "Tests", "Architecture", "Demo", "What I learned", "What I would do next"),
                PortfolioDrills.missingSections(readme));
    }

    @Test
    void aGoodBulletPassesEveryRule() {
        assertEquals(List.of(), PortfolioDrills.bulletProblems("Built a REST API with 14 endpoints, 308 automated tests and role-based access in Java 21"));
    }

    @Test
    void aWeakBulletIsToldEverythingThatIsWrongInOrder() {
        assertEquals(List.of("start with an action verb", "include a number", "do not say 'responsible for'"),
                PortfolioDrills.bulletProblems("Responsible for the shop backend and some things"));
        assertEquals(List.of("include a number"), PortfolioDrills.bulletProblems("Secured the API with BCrypt and roles"));
        assertEquals(List.of("start with an action verb"), PortfolioDrills.bulletProblems("Team player who wrote 5 tests"));
    }

    @Test
    void aBulletMustFitOnOneLine() {
        String longBullet = "Built " + "a very long description ".repeat(10) + "with 3 parts";
        assertEquals(List.of("keep it to one line of 160 characters or fewer"), PortfolioDrills.bulletProblems(longBullet));
    }

    @Test
    void timeLabelShowsMinutesAndSeconds() {
        assertEquals("4:55", PortfolioDrills.timeLabel(295));
        assertEquals("5:00", PortfolioDrills.timeLabel(300));
        assertEquals("1:01", PortfolioDrills.timeLabel(61));
        assertEquals("0:00", PortfolioDrills.timeLabel(0));
    }

    @Test
    void competentMeansEveryCriterionIsMetAndPercentIsTheGrade() {
        assertEquals("Competent", PortfolioDrills.decision(List.of(2, 2, 2, 2, 2)));
        assertEquals("Competent", PortfolioDrills.decision(List.of(3, 2, 3, 2, 2)));
        assertEquals(67, PortfolioDrills.percent(List.of(2, 2, 2, 2, 2)));
        assertEquals(100, PortfolioDrills.percent(List.of(3, 3)));
    }

    @Test
    void oneWeakCriterionCannotBeHiddenByStrongOnes() {
        assertEquals("Not yet competent", PortfolioDrills.decision(List.of(3, 3, 3, 3, 1)));
        assertEquals(87, PortfolioDrills.percent(List.of(3, 3, 3, 3, 1)), "a high percentage, and still not competent");
    }

    @Test
    void decisionRefusesNonsense() {
        assertThrows(IllegalArgumentException.class, () -> PortfolioDrills.decision(List.of()));
        assertThrows(IllegalArgumentException.class, () -> PortfolioDrills.decision(List.of(2, 4)));
        assertThrows(IllegalArgumentException.class, () -> PortfolioDrills.decision(List.of(-1)));
        assertThrows(IllegalArgumentException.class, () -> PortfolioDrills.percent(List.of()));
    }
}
