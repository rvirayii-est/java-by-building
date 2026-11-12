package dev.javabybuilding.exercises;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lesson 46 drills: the small rules behind "12-factor" configuration (settings come from the environment), as plain Java so you can
 * test them. Complete each TODO until Exercises46Test is green.
 */
public class ConfigDrills {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");

    /**
     * Replaces every {@code ${NAME}} and {@code ${NAME:default}} in a value with what the environment map says. A name that is missing
     * uses its default when there is one (an empty default {@code ${NAME:}} is allowed and gives ""); without a default it throws
     * IllegalArgumentException("Could not resolve placeholder 'NAME'"). Text without placeholders is returned unchanged.
     * resolve("${PORT:8080}", Map.of()) is "8080"; resolve("${PORT:8080}", Map.of("PORT", "9000")) is "9000".
     */
    public static String resolve(String value, Map<String, String> environment) {
        Matcher m = PLACEHOLDER.matcher(value);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String name = m.group(1);
            String replacement = environment.containsKey(name) ? environment.get(name) : m.group(2);
            if (replacement == null) {
                throw new IllegalArgumentException("Could not resolve placeholder '" + name + "'");
            }
            m.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(out);
        return out.toString();
    }

    /** How Spring Boot finds a property in the environment: upper case, and every '.' or '-' becomes '_'. toEnvName("shop.security.admin-password") is "SHOP_SECURITY_ADMIN_PASSWORD". */
    public static String toEnvName(String property) {
        return property.toUpperCase().replace('.', '_').replace('-', '_');
    }

    /**
     * Which value wins? The layers are given WEAKEST first (for example: defaults, the prod file, the environment, the command line).
     * The value from the LAST layer that has the key wins. Empty if no layer has it.
     */
    public static Optional<String> effective(List<Map<String, String>> layersWeakestFirst, String key) {
        String found = null;
        for (Map<String, String> layer : layersWeakestFirst) {
            if (layer.containsKey(key)) {
                found = layer.get(key);
            }
        }
        return Optional.ofNullable(found);
    }

    /**
     * Reads the lines of a .env file into a map (in file order). Blank lines and lines that start with '#' are skipped; a line is
     * NAME=value split at the FIRST '='; spaces around the name and the value are removed; a value wrapped in double quotes loses the quotes.
     * A line without '=' is skipped.
     */
    public static Map<String, String> parseDotEnv(List<String> lines) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String raw : lines) {
            String line = raw.strip();
            int eq = line.indexOf('=');
            if (line.isEmpty() || line.startsWith("#") || eq < 0) {
                continue;
            }
            String value = line.substring(eq + 1).strip();
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            values.put(line.substring(0, eq).strip(), value);
        }
        return values;
    }

    /** A copy that is safe to print or log: the value of any key whose name contains PASSWORD, SECRET, TOKEN or KEY (any case) becomes "****". */
    public static Map<String, String> redact(Map<String, String> settings) {
        Map<String, String> safe = new LinkedHashMap<>();
        settings.forEach((key, value) -> {
            String k = key.toUpperCase();
            boolean secret = k.contains("PASSWORD") || k.contains("SECRET") || k.contains("TOKEN") || k.contains("KEY");
            safe.put(key, secret ? "****" : value);
        });
        return safe;
    }
}
