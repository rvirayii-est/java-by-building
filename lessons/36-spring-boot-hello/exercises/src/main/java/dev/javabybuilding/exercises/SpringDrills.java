package dev.javabybuilding.exercises;

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
        // TODO 1: type.getSimpleName(), then lower-case the first character unless both of the first two are upper case
        // (Character.isUpperCase).
        return null;
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
        // TODO 2: loop: find "${" with indexOf, find the closing "}" after it, cut out the inside, split it at the FIRST ':' into key
        // and default (the default may be empty), look the key up, and rebuild the text in a StringBuilder.
        return null;
    }

    /**
     * Matches a request path against a pattern with {variables}, like @GetMapping("/products/{name}") and @PathVariable.
     * Returns the variables, or null when the path does not fit. Both must have the same number of parts; the parts that are not
     * {variables} must be equal. matchPath("/products/{name}", "/products/Rice") is {name=Rice}; matchPath("/products/{name}",
     * "/sales/7") is null. (No decoding needed here.)
     */
    public static Map<String, String> matchPath(String pattern, String path) {
        // TODO 3: split both on "/" and compare the parts one by one; a pattern part that starts with "{" and ends with "}" is a
        // variable (use substring to get its name). Return a LinkedHashMap.
        return null;
    }

    /**
     * Spring Boot reads the same setting from several places and the most specific one wins. The order, strongest first:
     * the command line, the environment, the properties file, the default. chooseSetting("server.port", cmd, env, file, "8080")
     * returns the value from the first map that contains the key, otherwise the default.
     */
    public static String chooseSetting(String key, Map<String, String> commandLine, Map<String, String> environment,
                                       Map<String, String> propertiesFile, String defaultValue) {
        // TODO 4: check commandLine, then environment, then propertiesFile with containsKey; fall back to defaultValue.
        return null;
    }

    /**
     * The properties files Spring Boot reads for the active profiles, in the order they are applied (later ones win):
     * always "application.properties" first, then "application-{profile}.properties" for each profile in the order given.
     * configFiles(List.of("dev", "h2")) is [application.properties, application-dev.properties, application-h2.properties].
     */
    public static List<String> configFiles(List<String> profiles) {
        // TODO 5: start a list with "application.properties" and add one name per profile.
        return null;
    }
}
