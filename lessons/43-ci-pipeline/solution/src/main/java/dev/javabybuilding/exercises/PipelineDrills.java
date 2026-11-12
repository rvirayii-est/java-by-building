package dev.javabybuilding.exercises;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String url = "https://github.com/" + owner + "/" + repo + "/actions/workflows/" + workflowFile;
        return "[![CI](" + url + "/badge.svg)](" + url + ")";
    }

    private static final Pattern USES = Pattern.compile("([\\w.-]+)/([\\w.-]+)@([\\w.-]+)");

    /** Reads "actions/checkout@v4" into an Action. Throws IllegalArgumentException("Not an action reference: ...") when it does not have that shape. */
    public static Action parseUses(String uses) {
        Matcher m = USES.matcher(uses == null ? "" : uses);
        if (!m.matches()) {
            throw new IllegalArgumentException("Not an action reference: " + uses);
        }
        return new Action(m.group(1), m.group(2), m.group(3));
    }

    /**
     * Is the reference pinned to something that does not move? A version tag (v4, v4.1, v4.1.2) or a full 40-character commit id is
     * pinned. A branch name (main, master) or no reference at all is not: the code behind it can change under you.
     */
    public static boolean isPinned(String uses) {
        try {
            String ref = parseUses(uses).ref();
            return ref.matches("v\\d+(\\.\\d+)*") || ref.matches("[0-9a-f]{40}");
        } catch (IllegalArgumentException notAnAction) {
            return false;
        }
    }

    /**
     * Which steps run? A pipeline is FAIL-FAST: steps run in order, and after the first failing step the rest are skipped.
     * failingIndex is the position (from 0) of the step that fails, or -1 when none fails. The failing step itself did run.
     * stepsThatRun(List.of("checkout", "build", "test"), 1) is ["checkout", "build"].
     */
    public static List<String> stepsThatRun(List<String> steps, int failingIndex) {
        if (failingIndex < 0 || failingIndex >= steps.size()) {
            return List.copyOf(steps);
        }
        return List.copyOf(steps.subList(0, failingIndex + 1));
    }

    /**
     * The key of a dependency cache: "maven-" + the operating system + "-" + the first 8 hex characters of the SHA-256 of the pom text.
     * Same pom, same key (the cache is reused). Change one dependency, the key changes (the cache is rebuilt). That is what
     * {@code cache: maven} in setup-java does, using hashFiles('**&#47;pom.xml').
     */
    public static String cacheKey(String os, String pomText) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(pomText.getBytes(StandardCharsets.UTF_8));
            return "maven-" + os + "-" + HexFormat.of().formatHex(hash).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e); // every Java has SHA-256
        }
    }
}
