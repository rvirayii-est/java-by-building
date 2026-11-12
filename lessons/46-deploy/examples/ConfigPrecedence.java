import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * "Same code, different places." JDK only. A program reads its settings from layers; a stronger layer overrides a weaker one.
 * This is how Spring Boot decides which value of server.port or shop.seed-sample the running app uses, in five lines of logic:
 * defaults in the jar, then the profile file, then environment variables, then command-line arguments.
 *
 * Run:  java ConfigPrecedence.java
 */
public class ConfigPrecedence {

    static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");

    /** ${NAME:default} -> the environment's NAME, else the default; ${NAME} with nothing to fall back on is an error (fail fast). */
    static String resolve(String value, Map<String, String> env) {
        Matcher m = PLACEHOLDER.matcher(value);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String found = env.containsKey(m.group(1)) ? env.get(m.group(1)) : m.group(2);
            if (found == null) {
                throw new IllegalStateException("Could not resolve placeholder '" + m.group(1) + "'");
            }
            m.appendReplacement(out, Matcher.quoteReplacement(found));
        }
        m.appendTail(out);
        return out.toString();
    }

    static Map<String, String> effective(Map<String, String> env, List<Map<String, String>> layersWeakestFirst) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map<String, String> layer : layersWeakestFirst) {
            layer.forEach((key, value) -> result.put(key, resolve(value, env)));
        }
        return result;
    }

    static void show(String title, Map<String, String> env, List<Map<String, String>> layers) {
        System.out.println("== " + title);
        try {
            effective(env, layers).forEach((key, value) -> System.out.println("   " + key + " = " + (key.contains("password") ? "****" : value)));
        } catch (IllegalStateException e) {
            System.out.println("   REFUSES TO START: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Map<String, String> applicationProperties = new LinkedHashMap<>(); // inside the jar: good for your own PC
        applicationProperties.put("server.port", "8080");
        applicationProperties.put("shop.seed-sample", "true");
        applicationProperties.put("shop.security.admin-password", "admin123");

        Map<String, String> prodFile = new LinkedHashMap<>(); // application-prod.properties: nothing secret has a default
        prodFile.put("server.port", "${PORT:8080}");
        prodFile.put("shop.seed-sample", "${SHOP_SEED_SAMPLE:false}");
        prodFile.put("shop.security.admin-password", "${SHOP_ADMIN_PASSWORD}");

        show("on your PC (no profile)", Map.of(), List.of(applicationProperties));
        show("prod, but nobody set the password", Map.of(), List.of(applicationProperties, prodFile));
        show("prod, on a host that sets PORT and the secret", Map.of("PORT", "10000", "SHOP_ADMIN_PASSWORD", "Str0ng-and-secret"), List.of(applicationProperties, prodFile));
        Map<String, String> commandLine = Map.of("server.port", "9090");
        show("prod, and someone also passes --server.port=9090", Map.of("SHOP_ADMIN_PASSWORD", "Str0ng-and-secret"), List.of(applicationProperties, prodFile, commandLine));
    }
}
