package dev.javabybuilding.ci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * A pipeline is a file, so it can be tested like code: the file must be valid YAML and must have the properties a good pipeline needs.
 * Two files are checked: the sample for your own project ({@code ci/student-ci.yml}, next to this module's pom.xml) and this
 * repository's own workflow. GitHub itself only finds out about a mistake after you push; this test finds it on your PC.
 *
 * <p>A YAML trap you will meet: in YAML 1.1 the bare word {@code on} means TRUE, so the parser files the "on:" section under the key
 * {@code true}. {@link #on(Map)} handles that.
 */
class CiWorkflowTest {

    private static final Path STUDENT_WORKFLOW = Path.of("ci", "student-ci.yml");
    private static final Path REPO_WORKFLOW = Path.of("..", "..", "..", ".github", "workflows", "ci.yml");
    private static final Pattern PINNED = Pattern.compile("[\\w.-]+/[\\w.-]+@(v\\d+(\\.\\d+)*|[0-9a-f]{40})");

    @SuppressWarnings("unchecked")
    private static Map<String, Object> load(Path file) throws IOException {
        Object parsed = new Yaml().load(Files.readString(file));
        assertTrue(parsed instanceof Map, file + " must be a YAML mapping");
        return (Map<String, Object>) parsed;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> on(Map<String, Object> workflow) {
        Object on = workflow.containsKey("on") ? workflow.get("on") : ((Map<Object, Object>) (Map<?, ?>) workflow).get(Boolean.TRUE);
        assertNotNull(on, "the workflow has no 'on:' section");
        return (Map<String, Object>) on;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> steps(Map<String, Object> workflow, String job) {
        Map<String, Object> jobs = (Map<String, Object>) workflow.get("jobs");
        assertNotNull(jobs, "the workflow has no jobs");
        Map<String, Object> theJob = (Map<String, Object>) jobs.get(job);
        assertNotNull(theJob, "no job called " + job);
        return (List<Map<String, Object>>) theJob.get("steps");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> job(Map<String, Object> workflow, String job) {
        return (Map<String, Object>) ((Map<String, Object>) workflow.get("jobs")).get(job);
    }

    private static boolean anyStepRuns(List<Map<String, Object>> steps, String command) {
        return steps.stream().anyMatch(step -> String.valueOf(step.get("run")).contains(command));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> stepUsing(List<Map<String, Object>> steps, String actionPrefix) {
        return steps.stream().filter(step -> String.valueOf(step.get("uses")).startsWith(actionPrefix)).findFirst()
                .orElseThrow(() -> new AssertionError("no step uses " + actionPrefix));
    }

    // ---- the sample pipeline for your own project ----

    @Test
    void theSampleRunsOnPushToMainAndOnPullRequests() throws IOException {
        Map<String, Object> on = on(load(STUDENT_WORKFLOW));
        assertTrue(on.containsKey("push"), "should run on push");
        assertTrue(on.containsKey("pull_request"), "should run on pull requests");
    }

    @Test
    void theSampleInstallsJava21WithAMavenCache() throws IOException {
        List<Map<String, Object>> steps = steps(load(STUDENT_WORKFLOW), "build");
        @SuppressWarnings("unchecked")
        Map<String, Object> with = (Map<String, Object>) stepUsing(steps, "actions/setup-java").get("with");
        assertEquals("21", String.valueOf(with.get("java-version")));
        assertEquals("temurin", with.get("distribution"));
        assertEquals("maven", with.get("cache"), "without a cache every run downloads the whole internet again");
    }

    @Test
    void theSampleChecksOutTheCodeBeforeItBuildsAndBuildsWithTheWrapper() throws IOException {
        List<Map<String, Object>> steps = steps(load(STUDENT_WORKFLOW), "build");
        assertEquals("actions/checkout@v4", steps.get(0).get("uses"), "the first step must get the code");
        assertTrue(anyStepRuns(steps, "./mvnw -B verify"), "the build step should be ./mvnw -B verify");
    }

    @Test
    void theSampleIsSafeAndCannotRunForever() throws IOException {
        Map<String, Object> workflow = load(STUDENT_WORKFLOW);
        @SuppressWarnings("unchecked")
        Map<String, Object> permissions = (Map<String, Object>) workflow.get("permissions");
        assertEquals(Map.of("contents", "read"), permissions, "give the build read-only access");
        assertNotNull(job(workflow, "build").get("timeout-minutes"), "set a timeout");
        assertEquals("ubuntu-latest", job(workflow, "build").get("runs-on"));
    }

    @Test
    void everyActionIsPinnedToAVersion() throws IOException {
        for (Path file : List.of(STUDENT_WORKFLOW, REPO_WORKFLOW)) {
            for (Map<String, Object> step : steps(load(file), file == STUDENT_WORKFLOW ? "build" : "verify")) {
                Object uses = step.get("uses");
                if (uses != null) {
                    assertTrue(PINNED.matcher(uses.toString()).matches(), file + ": '" + uses + "' must be pinned like actions/checkout@v4, not @main");
                }
            }
        }
    }

    @Test
    void theSampleHasNoSecretsInIt() throws IOException {
        String text = Files.readString(STUDENT_WORKFLOW).toLowerCase();
        assertFalse(text.contains("password"), "never write a password in a workflow");
        assertFalse(text.contains("ghp_"), "never paste a token into a workflow");
    }

    // ---- this repository's own pipeline (explained line by line in the lesson) ----

    @Test
    void theRepositoryPipelineRunsTheThreeChecksOfTheCourse() throws IOException {
        Map<String, Object> workflow = load(REPO_WORKFLOW);
        List<Map<String, Object>> steps = steps(workflow, "verify");
        assertTrue(anyStepRuns(steps, "./mvnw -B verify"), "reference solutions must build and pass");
        assertTrue(anyStepRuns(steps, "-Pstudent test-compile"), "starters and exercises must compile");
        assertTrue(anyStepRuns(steps, "scripts/run-examples.sh"), "every example must run");
        assertTrue(on(workflow).containsKey("pull_request"));
    }

    @Test
    void theRepositoryPipelineUsesTheMavenCacheAndJava21() throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> with = (Map<String, Object>) stepUsing(steps(load(REPO_WORKFLOW), "verify"), "actions/setup-java").get("with");
        assertEquals("maven", with.get("cache"));
        assertEquals("21", String.valueOf(with.get("java-version")));
    }
}
