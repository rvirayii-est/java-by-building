package dev.javabybuilding.exercises;

import java.util.ArrayList;
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
        int colon = reference.lastIndexOf(':');
        if (colon < 0 || colon < reference.lastIndexOf('/')) {
            return new Image(reference, "latest");
        }
        return new Image(reference.substring(0, colon), reference.substring(colon + 1));
    }

    /** The names of the stages: for "FROM x AS build" it is "build" (as written); a FROM without AS is called "stage-0", "stage-1"... by its position. Lines are not case-sensitive. */
    public static List<String> stageNames(String dockerfile) {
        List<String> names = new ArrayList<>();
        for (String line : dockerfile.lines().map(String::strip).toList()) {
            if (line.regionMatches(true, 0, "FROM ", 0, 5)) {
                String[] words = line.split("\\s+");
                names.add(words.length >= 4 && words[2].equalsIgnoreCase("AS") ? words[3] : "stage-" + names.size());
            }
        }
        return names;
    }

    /**
     * Would .dockerignore keep this path out of the build context? Supported patterns: an exact path or a folder name at the start
     * ("docs" matches "docs/a.md"), "**&#47;name" (that name at any depth: "**&#47;target" matches "a/b/target/x.class"), and "*.ext" (any file with
     * that ending). The first character '!' is not supported here.
     */
    public static boolean ignored(List<String> patterns, String path) {
        String[] parts = path.split("/");
        for (String pattern : patterns) {
            if (pattern.startsWith("**/")) {
                String name = pattern.substring(3);
                for (String part : parts) {
                    if (part.equals(name)) {
                        return true;
                    }
                }
            } else if (pattern.startsWith("*.")) {
                if (parts[parts.length - 1].endsWith(pattern.substring(1))) {
                    return true;
                }
            } else if (path.equals(pattern) || path.startsWith(pattern + "/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * A tiny linter. Returns, in this order, the problems found in a Dockerfile (an empty list means none):
     * "base image uses the latest tag" (any FROM image without a tag or with :latest; a stage name used as image does not count),
     * "no USER instruction: the container runs as root", "ENTRYPOINT or CMD is not in exec form" (the text after it does not start with '[').
     */
    public static List<String> lint(String dockerfile) {
        List<String> stages = new ArrayList<>();
        boolean latest = false;
        boolean user = false;
        boolean shellForm = false;
        for (String line : dockerfile.lines().map(String::strip).toList()) {
            String upper = line.toUpperCase();
            if (upper.startsWith("FROM ")) {
                String[] words = line.split("\\s+");
                if (!stages.contains(words[1]) && parseImage(words[1]).tag().equals("latest")) {
                    latest = true;
                }
                if (words.length >= 4) {
                    stages.add(words[3]);
                }
            } else if (upper.startsWith("USER ")) {
                user = true;
            } else if ((upper.startsWith("ENTRYPOINT ") || upper.startsWith("CMD ")) && !line.substring(line.indexOf(' ')).strip().startsWith("[")) {
                shellForm = true;
            }
        }
        List<String> problems = new ArrayList<>();
        if (latest) {
            problems.add("base image uses the latest tag");
        }
        if (!user) {
            problems.add("no USER instruction: the container runs as root");
        }
        if (shellForm) {
            problems.add("ENTRYPOINT or CMD is not in exec form");
        }
        return problems;
    }
}
