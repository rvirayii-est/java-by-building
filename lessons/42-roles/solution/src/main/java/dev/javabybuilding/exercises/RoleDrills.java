package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Lesson 42 drills: a tiny version of the rules table in SecurityConfig, so you can see how "the first rule that matches decides"
 * works. Complete each TODO until Exercises42Test is green.
 */
public class RoleDrills {

    /** One rule: a method ("*" means any), a path pattern, and the roles that may pass (an EMPTY set means everybody, even strangers). */
    public record Rule(String method, String pattern, Set<String> roles) {}

    /**
     * Does a path pattern match a path? "*" stands for exactly ONE path part, "**" for the rest of the path (zero or more parts).
     * Parts are separated by '/'. matches("/products/*", "/products/rice") is true; matches("/products/*", "/products") is false;
     * matches("/products/**", "/products") is true; matches("/products/**", "/products/rice/stock") is true.
     */
    public static boolean matches(String pattern, String path) {
        String[] p = pattern.split("/");
        String[] q = path.split("/");
        for (int i = 0; i < p.length; i++) {
            if (p[i].equals("**")) {
                return true;
            }
            if (i >= q.length) {
                return false;
            }
            if (!p[i].equals("*") && !p[i].equals(q[i])) {
                return false;
            }
        }
        return p.length == q.length;
    }

    /** From the authorities Spring Security hands out ("ROLE_ADMIN", "ROLE_CASHIER") to the role names ("ADMIN", "CASHIER"), sorted. Other authorities are ignored. */
    public static Set<String> rolesFromAuthorities(List<String> authorities) {
        Set<String> roles = new TreeSet<>();
        for (String authority : authorities) {
            if (authority.startsWith("ROLE_")) {
                roles.add(authority.substring("ROLE_".length()));
            }
        }
        return roles;
    }

    /**
     * Is the request allowed? The FIRST rule whose method and pattern match decides: it allows when its role set is empty or shares a
     * role with userRoles. When NO rule matches, the answer is false (deny by default).
     */
    public static boolean allowed(List<Rule> rules, String method, String path, Set<String> userRoles) {
        for (Rule rule : rules) {
            if ((rule.method().equals("*") || rule.method().equals(method)) && matches(rule.pattern(), path)) {
                return rule.roles().isEmpty() || rule.roles().stream().anyMatch(userRoles::contains);
            }
        }
        return false;
    }

    /**
     * Says WHY: "ALLOWED by rule 2", "DENIED by rule 3" (rules are numbered from 1), or "DENIED: no rule matches". This is the
     * sentence you want in a log when a user asks "why can't I?".
     */
    public static String explain(List<Rule> rules, String method, String path, Set<String> userRoles) {
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            if ((rule.method().equals("*") || rule.method().equals(method)) && matches(rule.pattern(), path)) {
                boolean ok = rule.roles().isEmpty() || rule.roles().stream().anyMatch(userRoles::contains);
                return (ok ? "ALLOWED" : "DENIED") + " by rule " + (i + 1);
            }
        }
        return "DENIED: no rule matches";
    }
}
