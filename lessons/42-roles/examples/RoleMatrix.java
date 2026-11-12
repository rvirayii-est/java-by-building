import java.util.List;
import java.util.Set;

/**
 * "The first rule that matches decides." JDK only. The shop's rules as data, a table of who may do what, and the classic mistake:
 * a broad rule placed BEFORE a specific one hides it. Spring Security's authorizeHttpRequests(...) works the same way.
 *
 * Run:  java RoleMatrix.java
 */
public class RoleMatrix {

    record Rule(String method, String path, Set<String> roles) {} // roles empty = everybody

    static boolean matches(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            return path.equals(pattern.substring(0, pattern.length() - 3)) || path.startsWith(pattern.substring(0, pattern.length() - 2));
        }
        return pattern.equals(path);
    }

    static String decide(List<Rule> rules, String method, String path, String role) {
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            if ((rule.method().equals("*") || rule.method().equals(method)) && matches(rule.path(), path)) {
                boolean ok = rule.roles().isEmpty() || rule.roles().contains(role);
                return (ok ? "yes" : "NO ") + " (rule " + (i + 1) + ")";
            }
        }
        return "NO  (no rule)";
    }

    public static void main(String[] args) {
        List<Rule> good = List.of(
                new Rule("GET", "/products/**", Set.of("ADMIN", "CASHIER")),
                new Rule("POST", "/sales", Set.of("ADMIN", "CASHIER")),
                new Rule("*", "/products/**", Set.of("ADMIN")),
                new Rule("*", "/reports/**", Set.of("ADMIN")));
        String[][] requests = {{"GET", "/products"}, {"POST", "/products"}, {"DELETE", "/products/rice"}, {"POST", "/sales"}, {"GET", "/reports/stock"}, {"GET", "/other"}};

        System.out.printf("%-26s %-14s %-14s%n", "request", "ADMIN", "CASHIER");
        for (String[] r : requests) {
            System.out.printf("%-26s %-14s %-14s%n", r[0] + " " + r[1], decide(good, r[0], r[1], "ADMIN"), decide(good, r[0], r[1], "CASHIER"));
        }

        // The classic mistake: the broad "admin only" rule comes first, so the cashier can no longer read products.
        List<Rule> mistaken = List.of(good.get(2), good.get(0), good.get(1), good.get(3));
        System.out.println();
        System.out.println("Same rules, broad one first. Cashier reads GET /products: " + decide(mistaken, "GET", "/products", "CASHIER"));
        System.out.println("Specific rules first (correct).  Cashier reads GET /products: " + decide(good, "GET", "/products", "CASHIER"));
    }
}
