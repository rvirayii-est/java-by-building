package dev.javabybuilding.exercises;

import java.util.List;

/**
 * Lesson 45 drills: tiny readers and checkers for Docker files, so the Dockerfile stops being a mystery.
 * Complete each TODO until Exercises45Test is green.
 */
public class DockerDrills {

    /** An image reference split in two. */
    public record Image(String name, String tag) {}

    /**
     * Splits "eclipse-temurin:21-jre" into name "eclipse-temurin" and tag "21-jre". No tag means "latest". A colon that belongs to a
     * registry port does not count as a tag separator: "localhost:5000/shop" has name "localhost:5000/shop" and tag "latest".
     */
    public static Image parseImage(String reference) {
        // TODO 1: int colon = reference.lastIndexOf(':'). There is a tag only if colon >= 0 AND colon is after the last '/'.
        return null;
    }

    /** The names of the stages: for "FROM x AS build" it is "build" (as written); a FROM without AS is called "stage-0", "stage-1"... by its position. Lines are not case-sensitive. */
    public static List<String> stageNames(String dockerfile) {
        // TODO 2: for each stripped line that starts with FROM (ignore case: line.regionMatches(true, 0, "FROM ", 0, 5)),
        // split on whitespace: [FROM, image, AS, name] has 4 words. The position is the number of stages found so far.
        return null;
    }

    /**
     * Would .dockerignore keep this path out of the build context? Supported patterns: an exact path or a folder name at the start
     * ("docs" matches "docs/a.md"), "**&#47;name" (that name at any depth: "**&#47;target" matches "a/b/target/x.class"), and "*.ext" (any file with
     * that ending). The first character '!' is not supported here.
     */
    public static boolean ignored(List<String> patterns, String path) {
        // TODO 3: split the path on "/". For each pattern: starts with "**/" -> any part equals the rest; starts with "*." -> the last part
        // endsWith(pattern.substring(1)); otherwise path.equals(pattern) || path.startsWith(pattern + "/").
        return false;
    }

    /**
     * A tiny linter. Returns, in this order, the problems found in a Dockerfile (an empty list means none):
     * "base image uses the latest tag" (any FROM image without a tag or with :latest; a stage name used as image does not count),
     * "no USER instruction: the container runs as root", "ENTRYPOINT or CMD is not in exec form" (the text after it does not start with '[').
     */
    public static List<String> lint(String dockerfile) {
        // TODO 4: one pass over the lines remembering three booleans (latest, user, shellForm) and the stage names seen so far; then build the list.
        return null;
    }
}
