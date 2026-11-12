package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.exercises.RoleDrills.Rule;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class Exercises42Test {

    private static final List<Rule> RULES = List.of(
            new Rule("GET", "/hello", Set.of()),
            new Rule("GET", "/products/**", Set.of("ADMIN", "CASHIER")),
            new Rule("*", "/products/**", Set.of("ADMIN")),
            new Rule("*", "/reports/**", Set.of("ADMIN")));

    @Test
    void starMatchesExactlyOnePart() {
        assertTrue(RoleDrills.matches("/products/*", "/products/rice"));
        assertFalse(RoleDrills.matches("/products/*", "/products"));
        assertFalse(RoleDrills.matches("/products/*", "/products/rice/stock"));
        assertTrue(RoleDrills.matches("/products", "/products"));
        assertFalse(RoleDrills.matches("/products", "/sales"));
    }

    @Test
    void doubleStarMatchesTheRestOfThePath() {
        assertTrue(RoleDrills.matches("/products/**", "/products"));
        assertTrue(RoleDrills.matches("/products/**", "/products/rice/stock"));
        assertFalse(RoleDrills.matches("/products/**", "/sales/1"));
    }

    @Test
    void rolesComeFromAuthoritiesThatStartWithRole() {
        assertEquals(Set.of("ADMIN", "CASHIER"), RoleDrills.rolesFromAuthorities(List.of("ROLE_CASHIER", "SCOPE_read", "ROLE_ADMIN")));
        assertEquals(Set.of(), RoleDrills.rolesFromAuthorities(List.of()));
    }

    @Test
    void theFirstMatchingRuleDecides() {
        assertTrue(RoleDrills.allowed(RULES, "GET", "/products/rice", Set.of("CASHIER")));   // rule 2
        assertFalse(RoleDrills.allowed(RULES, "DELETE", "/products/rice", Set.of("CASHIER"))); // rule 3
        assertTrue(RoleDrills.allowed(RULES, "DELETE", "/products/rice", Set.of("ADMIN")));
        assertTrue(RoleDrills.allowed(RULES, "GET", "/hello", Set.of()));                      // empty roles = everybody
    }

    @Test
    void noRuleMeansDenied() {
        assertFalse(RoleDrills.allowed(RULES, "GET", "/secret", Set.of("ADMIN")));
    }

    @Test
    void theOrderOfRulesMatters() {
        List<Rule> broadFirst = List.of(new Rule("*", "/products/**", Set.of("ADMIN")), new Rule("GET", "/products/**", Set.of("CASHIER")));
        assertFalse(RoleDrills.allowed(broadFirst, "GET", "/products/rice", Set.of("CASHIER")), "the broad rule shadows the specific one");
    }

    @Test
    void explainSaysWhichRuleDecided() {
        assertEquals("ALLOWED by rule 2", RoleDrills.explain(RULES, "GET", "/products", Set.of("CASHIER")));
        assertEquals("DENIED by rule 3", RoleDrills.explain(RULES, "POST", "/products", Set.of("CASHIER")));
        assertEquals("DENIED: no rule matches", RoleDrills.explain(RULES, "GET", "/secret", Set.of("ADMIN")));
    }
}
