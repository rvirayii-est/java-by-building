package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.exercises.AuthDrills.Credentials;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class Exercises41Test {

    @Test
    void basicHeaderIsTheWordBasicPlusBase64OfUserColonPassword() {
        assertEquals("Basic YWRtaW46YWRtaW4xMjM=", AuthDrills.basicHeader("admin", "admin123"));
    }

    @Test
    void parseBasicHeaderReadsItBackAndSplitsAtTheFirstColon() {
        assertEquals(Optional.of(new Credentials("admin", "admin123")), AuthDrills.parseBasicHeader("Basic YWRtaW46YWRtaW4xMjM="));
        assertEquals(Optional.of(new Credentials("ana", "pa:ss:word")), AuthDrills.parseBasicHeader(AuthDrills.basicHeader("ana", "pa:ss:word")));
    }

    @Test
    void parseBasicHeaderRefusesAnythingElse() {
        assertEquals(Optional.empty(), AuthDrills.parseBasicHeader(null));
        assertEquals(Optional.empty(), AuthDrills.parseBasicHeader("Bearer abc"));
        assertEquals(Optional.empty(), AuthDrills.parseBasicHeader("Basic !!!"));
        assertEquals(Optional.empty(), AuthDrills.parseBasicHeader("Basic bm9jb2xvbg==")); // "nocolon"
    }

    @Test
    void passwordProblemsAreListedInAFixedOrder() {
        assertEquals(List.of(), AuthDrills.passwordProblems("admin123"));
        assertEquals(List.of("must be at least 8 characters", "must contain a digit"), AuthDrills.passwordProblems("abc"));
        assertEquals(List.of("must contain a letter"), AuthDrills.passwordProblems("12345678"));
        assertEquals(List.of("must contain a digit", "must not contain spaces"), AuthDrills.passwordProblems("with spaces"));
        assertEquals(List.of("must be at least 8 characters", "must contain a letter", "must contain a digit"), AuthDrills.passwordProblems(null));
    }

    @Test
    void statusForSeparates401From403() {
        assertEquals(401, AuthDrills.statusFor(false, false, true));
        assertEquals(401, AuthDrills.statusFor(true, false, true));
        assertEquals(403, AuthDrills.statusFor(true, true, false));
        assertEquals(200, AuthDrills.statusFor(true, true, true));
    }

    @Test
    void maskHidesAllButTheFirstCharacter() {
        assertEquals("a*******", AuthDrills.mask("admin123"));
        assertEquals("x", AuthDrills.mask("x"));
        assertEquals("", AuthDrills.mask(""));
        assertEquals("", AuthDrills.mask(null));
    }

    @Test
    void looksLikeBcryptRecognisesTheRealThing() {
        String real = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"; // a real BCrypt hash (60 characters)
        assertTrue(AuthDrills.looksLikeBcrypt(real));
        assertFalse(AuthDrills.looksLikeBcrypt("admin123"));
        assertFalse(AuthDrills.looksLikeBcrypt(null));
        assertFalse(AuthDrills.looksLikeBcrypt("$2a$10$tooShort"));
    }
}
