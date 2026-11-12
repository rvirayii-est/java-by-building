package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Set;

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
        // TODO 1: split both on "/" (pattern.split("/")). Walk the pattern parts: "**" -> return true; a path that has run out -> false;
        // a part that is not "*" must equal the path part. At the end the two must have the same number of parts.
        return false;
    }

    /** From the authorities Spring Security hands out ("ROLE_ADMIN", "ROLE_CASHIER") to the role names ("ADMIN", "CASHIER"), sorted. Other authorities are ignored. */
    public static Set<String> rolesFromAuthorities(List<String> authorities) {
        // TODO 2: a TreeSet (it keeps its items sorted); for each text that startsWith("ROLE_") add substring("ROLE_".length()).
        return null;
    }

    /**
     * Is the request allowed? The FIRST rule whose method and pattern match decides: it allows when its role set is empty or shares a
     * role with userRoles. When NO rule matches, the answer is false (deny by default).
     */
    public static boolean allowed(List<Rule> rules, String method, String path, Set<String> userRoles) {
        // TODO 3: loop over the rules in order; the method matches when it is "*" or equal; use matches(...) for the path; RETURN at the first match.
        throw new UnsupportedOperationException("TODO 3: write allowed(...)"); // a stub that says "no" would make some tests pass by accident
    }

    /**
     * Says WHY: "ALLOWED by rule 2", "DENIED by rule 3" (rules are numbered from 1), or "DENIED: no rule matches". This is the
     * sentence you want in a log when a user asks "why can't I?".
     */
    public static String explain(List<Rule> rules, String method, String path, Set<String> userRoles) {
        // TODO 4: the same loop as allowed, but with an index, so you can say which rule decided.
        return null;
    }
}
