package dev.javabybuilding.exercises;

import java.util.List;

/**
 * Lesson 43 drills: the small rules behind a CI pipeline, as plain Java so you can test them. Complete each TODO until
 * Exercises43Test is green.
 */
public class PipelineDrills {

    /** A step of the form {@code owner/name@ref}, such as actions/checkout@v4. */
    public record Action(String owner, String name, String ref) {}

    /**
     * The Markdown for a status badge: a picture (made by GitHub) that links to the workflow's runs. The result is
     * {@code [![CI](https://github.com/OWNER/REPO/actions/workflows/FILE/badge.svg)](https://github.com/OWNER/REPO/actions/workflows/FILE)}.
     * badgeMarkdown("ana", "shop", "ci.yml") gives
     * {@code [![CI](https://github.com/ana/shop/actions/workflows/ci.yml/badge.svg)](https://github.com/ana/shop/actions/workflows/ci.yml)}.
     */
    public static String badgeMarkdown(String owner, String repo, String workflowFile) {
        // TODO 1: build the URL once ("https://github.com/" + owner + "/" + repo + "/actions/workflows/" + workflowFile), then use it twice.
        return null;
    }

    /** Reads "actions/checkout@v4" into an Action. Throws IllegalArgumentException("Not an action reference: ...") when it does not have that shape. */
    public static Action parseUses(String uses) {
        // TODO 2: a regular expression ([\w.-]+)/([\w.-]+)@([\w.-]+) with Pattern.matcher(...).matches(); null counts as not matching.
        return null;
    }

    /**
     * Is the reference pinned to something that does not move? A version tag (v4, v4.1, v4.1.2) or a full 40-character commit id is
     * pinned. A branch name (main, master) or no reference at all is not: the code behind it can change under you.
     */
    public static boolean isPinned(String uses) {
        // TODO 3: use parseUses (catch IllegalArgumentException: not pinned), then ref.matches("v\\d+(\\.\\d+)*") or ref.matches("[0-9a-f]{40}").
        return false;
    }

    /**
     * Which steps run? A pipeline is FAIL-FAST: steps run in order, and after the first failing step the rest are skipped.
     * failingIndex is the position (from 0) of the step that fails, or -1 when none fails. The failing step itself did run.
     * stepsThatRun(List.of("checkout", "build", "test"), 1) is ["checkout", "build"].
     */
    public static List<String> stepsThatRun(List<String> steps, int failingIndex) {
        // TODO 4: steps.subList(0, failingIndex + 1) when failingIndex is inside the list, otherwise all the steps.
        return null;
    }

    /**
     * The key of a dependency cache: "maven-" + the operating system + "-" + the first 8 hex characters of the SHA-256 of the pom text.
     * Same pom, same key (the cache is reused). Change one dependency, the key changes (the cache is rebuilt). That is what
     * {@code cache: maven} in setup-java does, using hashFiles of every pom.xml.
     */
    public static String cacheKey(String os, String pomText) {
        // TODO 5: MessageDigest.getInstance("SHA-256").digest(pomText.getBytes(UTF_8)), HexFormat.of().formatHex(bytes).substring(0, 8).
        return null;
    }
}
