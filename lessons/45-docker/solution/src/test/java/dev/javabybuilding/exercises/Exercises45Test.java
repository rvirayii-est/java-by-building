package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.exercises.DockerDrills.Image;
import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises45Test {

    private static final String GOOD = """
            FROM eclipse-temurin:21-jdk AS build
            RUN echo build
            FROM eclipse-temurin:21-jre
            USER shop
            ENTRYPOINT ["java", "-jar", "app.jar"]
            """;

    @Test
    void parseImageSplitsNameAndTag() {
        assertEquals(new Image("eclipse-temurin", "21-jre"), DockerDrills.parseImage("eclipse-temurin:21-jre"));
        assertEquals(new Image("alpine", "latest"), DockerDrills.parseImage("alpine"));
    }

    @Test
    void aRegistryPortIsNotATag() {
        assertEquals(new Image("localhost:5000/shop", "latest"), DockerDrills.parseImage("localhost:5000/shop"));
        assertEquals(new Image("localhost:5000/shop", "1.0"), DockerDrills.parseImage("localhost:5000/shop:1.0"));
    }

    @Test
    void stageNamesComeFromAsOrFromPosition() {
        assertEquals(List.of("build", "stage-1"), DockerDrills.stageNames(GOOD));
        assertEquals(List.of("a", "b"), DockerDrills.stageNames("from x:1 as a\nFROM y:2 AS b"));
    }

    @Test
    void ignoredUnderstandsFoldersAnyDepthNamesAndExtensions() {
        List<String> patterns = List.of(".git", "**/target", "*.log", "docs");
        assertTrue(DockerDrills.ignored(patterns, ".git/config"));
        assertTrue(DockerDrills.ignored(patterns, "lessons/45/solution/target/shop-api.jar"));
        assertTrue(DockerDrills.ignored(patterns, "build.log"));
        assertTrue(DockerDrills.ignored(patterns, "docs/SPEC.md"));
        assertFalse(DockerDrills.ignored(patterns, "lessons/45/solution/src/Main.java"));
        assertFalse(DockerDrills.ignored(patterns, "documents/a.md"));
    }

    @Test
    void aGoodDockerfileHasNoProblems() {
        assertEquals(List.of(), DockerDrills.lint(GOOD));
    }

    @Test
    void lintFindsTheThreeClassicMistakesInOrder() {
        String bad = """
                FROM eclipse-temurin
                COPY app.jar /app.jar
                ENTRYPOINT java -jar /app.jar
                """;
        assertEquals(List.of("base image uses the latest tag", "no USER instruction: the container runs as root",
                "ENTRYPOINT or CMD is not in exec form"), DockerDrills.lint(bad));
    }

    @Test
    void aStageNameUsedAsAnImageIsNotLatest() {
        String stages = "FROM eclipse-temurin:21-jdk AS build\nFROM build\nUSER app\nCMD [\"x\"]";
        assertEquals(List.of(), DockerDrills.lint(stages));
    }
}
