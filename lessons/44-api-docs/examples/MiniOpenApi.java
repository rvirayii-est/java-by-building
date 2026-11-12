import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * How springdoc can describe your API without you writing the description: it READS your controllers. This example does the same
 * thing in 50 lines with Java's reflection (code that looks at code): it finds the methods marked @Route and prints an OpenAPI-style
 * description. JDK only. The real thing (springdoc) also reads parameter types, return types and validation rules.
 *
 * Run:  java MiniOpenApi.java
 */
public class MiniOpenApi {

    @Retention(RetentionPolicy.RUNTIME)
    @interface Route {
        String method();
        String path();
        String summary();
    }

    /** A pretend controller. Its annotations ARE the documentation. */
    static class ProductApi {
        @Route(method = "get", path = "/products", summary = "List the products")
        public void list() {}

        @Route(method = "get", path = "/products/{name}", summary = "Read one product")
        public void one() {}

        @Route(method = "post", path = "/products", summary = "Create a product")
        public void create() {}
    }

    public static void main(String[] args) {
        List<Route> routes = new ArrayList<>();
        for (Method method : ProductApi.class.getDeclaredMethods()) {
            Route route = method.getAnnotation(Route.class);
            if (route != null) {
                routes.add(route);
            }
        }
        routes.sort(Comparator.comparing(Route::path).thenComparing(Route::method));

        Map<String, List<Route>> byPath = new LinkedHashMap<>(); // one entry per path; the methods of a path go inside it
        for (Route route : routes) {
            byPath.computeIfAbsent(route.path(), p -> new ArrayList<>()).add(route);
        }
        System.out.println("{\"openapi\":\"3.0.1\",\"info\":{\"title\":\"Mini API\",\"version\":\"1.0\"},\"paths\":{");
        int left = byPath.size();
        for (var entry : byPath.entrySet()) {
            String operations = entry.getValue().stream()
                    .map(r -> "\"" + r.method() + "\":{\"summary\":\"" + r.summary() + "\"}")
                    .collect(Collectors.joining(","));
            System.out.printf("  \"%s\":{%s}%s%n", entry.getKey(), operations, --left > 0 ? "," : "");
        }
        System.out.println("}}");
        System.out.println("Documented " + routes.size() + " operations without writing a single line of description by hand.");
    }
}
