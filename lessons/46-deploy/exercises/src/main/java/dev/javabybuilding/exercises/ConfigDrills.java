package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lesson 46 drills: the small rules behind "12-factor" configuration (settings come from the environment), as plain Java so you can
 * test them. Complete each TODO until Exercises46Test is green.
 */
public class ConfigDrills {

    /**
     * Replaces every {@code ${NAME}} and {@code ${NAME:default}} in a value with what the environment map says. A name that is missing
     * uses its default when there is one (an empty default {@code ${NAME:}} is allowed and gives ""); without a default it throws
     * IllegalArgumentException("Could not resolve placeholder 'NAME'"). Text without placeholders is returned unchanged.
     * resolve("${PORT:8080}", Map.of()) is "8080"; resolve("${PORT:8080}", Map.of("PORT", "9000")) is "9000".
     */
    public static String resolve(String value, Map<String, String> environment) {
        // TODO 1: Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}"); loop with matcher.find(); group(1) is the name, group(2) the default (null if absent);
        // use matcher.appendReplacement(buffer, Matcher.quoteReplacement(text)) and appendTail.
        return null;
    }

    /** How Spring Boot finds a property in the environment: upper case, and every '.' or '-' becomes '_'. toEnvName("shop.security.admin-password") is "SHOP_SECURITY_ADMIN_PASSWORD". */
    public static String toEnvName(String property) {
        // TODO 2: toUpperCase() and two replace(...) calls.
        return null;
    }

    /**
     * Which value wins? The layers are given WEAKEST first (for example: defaults, the prod file, the environment, the command line).
     * The value from the LAST layer that has the key wins. Empty if no layer has it.
     */
    public static Optional<String> effective(List<Map<String, String>> layersWeakestFirst, String key) {
        // TODO 3: go through the layers in order and remember the value of every layer that containsKey(key); the last one remembered wins.
        return null;
    }

    /**
     * Reads the lines of a .env file into a map (in file order). Blank lines and lines that start with '#' are skipped; a line is
     * NAME=value split at the FIRST '='; spaces around the name and the value are removed; a value wrapped in double quotes loses the quotes.
     * A line without '=' is skipped.
     */
    public static Map<String, String> parseDotEnv(List<String> lines) {
        // TODO 4: a LinkedHashMap (keeps the order); strip() each line; skip empty, '#', and lines without '='; indexOf('=') splits name and value.
        return null;
    }

    /** A copy that is safe to print or log: the value of any key whose name contains PASSWORD, SECRET, TOKEN or KEY (any case) becomes "****". */
    public static Map<String, String> redact(Map<String, String> settings) {
        // TODO 5: copy into a new LinkedHashMap; test key.toUpperCase().contains(...) for the four words.
        return null;
    }
}
