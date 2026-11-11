package dev.javabybuilding.exercises;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lesson 36 drills: tiny versions of things Spring does for you, written in plain Java so you know there is no magic.
 * Complete each TODO until Exercises36Test is green.
 */
public class SpringDrills {

    /**
     * The name Spring gives a bean made from a class: the class name with the first letter in lower case.
     * beanName(ProductController.class) is "productController". One exception: when the FIRST TWO letters are capitals
     * the name stays as it is (URLShortener stays URLShortener), the same rule as java.beans.Introspector.decapitalize.
     */
    public static String beanName(Class<?> type) {
        String name = type.getSimpleName();
        if (name.length() > 1 && Character.isUpperCase(name.charAt(0)) && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Fills in placeholders like Spring's @Value("${key}") and @Value("${key:default}"): "${port}" becomes the value of
     * port, "${host:localhost}" becomes the value of host or "localhost" when there is none. Text without placeholders is
     * returned as it is, and several placeholders in one text are all replaced.
     * resolve("http://${host:localhost}:${port}/", Map.of("port", "8080")) is "http://localhost:8080/".
     *
     * @throws IllegalArgumentException "Could not resolve placeholder 'port'" when a key has no value and no default
     */
    public static String resolve(String text, Map<String, String> properties) {
        StringBuilder result = new StringBuilder();
        int position = 0;
        while (true) {
            int open = text.indexOf("${", position);
            int close = open < 0 ? -1 : text.indexOf('}', open);
            if (open < 0 || close < 0) {
                return result.append(text.substring(position)).toString();
            }
            result.append(text, position, open);
            String inside = text.substring(open + 2, close);
            int colon = inside.indexOf(':');
            String key = colon < 0 ? inside : inside.substring(0, colon);
            String value = properties.get(key);
            if (value == null && colon >= 0) {
                value = inside.substring(colon + 1);
            }
            if (value == null) {
                throw new IllegalArgumentException("Could not resolve placeholder '" + key + "'");
            }
            result.append(value);
            position = close + 1;
        }
    }

    /**
     * Matches a request path against a pattern with {variables}, like @GetMapping("/products/{name}") and @PathVariable.
     * Returns the variables, or null when the path does not fit. Both must have the same number of parts; the parts that are not
     * {variables} must be equal. matchPath("/products/{name}", "/products/Rice") is {name=Rice}; matchPath("/products/{name}",
     * "/sales/7") is null. (No decoding needed here.)
     */
    public static Map<String, String> matchPath(String pattern, String path) {
        String[] patternParts = pattern.split("/");
        String[] pathParts = path.split("/");
        if (patternParts.length != pathParts.length) {
            return null;
        }
        Map<String, String> variables = new LinkedHashMap<>();
        for (int i = 0; i < patternParts.length; i++) {
            String expected = patternParts[i];
            if (expected.startsWith("{") && expected.endsWith("}")) {
                variables.put(expected.substring(1, expected.length() - 1), pathParts[i]);
            } else if (!expected.equals(pathParts[i])) {
                return null;
            }
        }
        return variables;
    }

    /**
     * Spring Boot reads the same setting from several places and the most specific one wins. The order, strongest first:
     * the command line, the environment, the properties file, the default. chooseSetting("server.port", cmd, env, file, "8080")
     * returns the value from the first map that contains the key, otherwise the default.
     */
    public static String chooseSetting(String key, Map<String, String> commandLine, Map<String, String> environment,
                                       Map<String, String> propertiesFile, String defaultValue) {
        for (Map<String, String> source : List.of(commandLine, environment, propertiesFile)) {
            if (source.containsKey(key)) {
                return source.get(key);
            }
        }
        return defaultValue;
    }

    /**
     * The properties files Spring Boot reads for the active profiles, in the order they are applied (later ones win):
     * always "application.properties" first, then "application-{profile}.properties" for each profile in the order given.
     * configFiles(List.of("dev", "h2")) is [application.properties, application-dev.properties, application-h2.properties].
     */
    public static List<String> configFiles(List<String> profiles) {
        List<String> files = new ArrayList<>(List.of("application.properties"));
        for (String profile : profiles) {
            files.add("application-" + profile + ".properties");
        }
        return files;
    }
}
