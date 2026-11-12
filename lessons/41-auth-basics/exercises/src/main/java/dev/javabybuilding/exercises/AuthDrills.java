package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Optional;

/**
 * Lesson 41 drills: the small pieces behind HTTP Basic login, written by hand so you can see there is no magic. (In the real app
 * Spring Security does all of this. And passwords are NEVER hashed by hand: BCrypt comes from the library.) Complete each TODO
 * until Exercises41Test is green.
 */
public class AuthDrills {

    /** A username and a password, as found in an Authorization header. */
    public record Credentials(String username, String password) {}

    /**
     * Builds the value of the Authorization header for HTTP Basic: the word "Basic", a space, and the Base64 text of
     * "username:password" (UTF-8). basicHeader("admin", "admin123") is "Basic YWRtaW46YWRtaW4xMjM=".
     */
    public static String basicHeader(String username, String password) {
        // TODO 1: java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8))
        return null;
    }

    /**
     * The opposite: reads a header value back into credentials. Returns Optional.empty() when the header is null, does not start with
     * "Basic " (any other scheme, such as Bearer), is not valid Base64, or has no ':' in it. The password may itself contain ':' so
     * split at the FIRST colon only.
     */
    public static Optional<Credentials> parseBasicHeader(String header) {
        // TODO 2: check the prefix, Base64.getDecoder().decode(...) inside try/catch (IllegalArgumentException), then indexOf(':').
        return null;
    }

    /**
     * What is wrong with a new password? Returns the problems in this fixed order (empty list = fine):
     * "must be at least 8 characters", "must contain a letter", "must contain a digit", "must not contain spaces".
     * A null password has every problem except the spaces one.
     */
    public static List<String> passwordProblems(String password) {
        // TODO 3: treat null as "". Add each message to a list when its rule is broken (Character.isLetter, Character.isDigit).
        return null;
    }

    /**
     * The status code of a request, decided like Spring Security does: no credentials, or wrong ones: 401 (we do not know who you are);
     * right credentials but not allowed to do this: 403 (we know you, and the answer is no); otherwise 200.
     */
    public static int statusFor(boolean sentCredentials, boolean credentialsAreValid, boolean allowed) {
        // TODO 4: three branches. Order matters: check "do we know you?" before "may you?".
        return 0;
    }

    /** For logs: shows only the first character and hides the rest with '*', same length. mask("admin123") is "a*******"; null or "" gives "". */
    public static String mask(String secret) {
        // TODO 5: secret.charAt(0) + "*".repeat(secret.length() - 1), after handling null and "".
        return null;
    }

    /** Does this text look like a BCrypt hash? "$2a$10$" + 53 characters of ./A-Za-z0-9 (60 in all). null is false. */
    public static boolean looksLikeBcrypt(String text) {
        // TODO 6: a regular expression: \$2[aby]\$\d{2}\$[./A-Za-z0-9]{53} with Pattern.matcher(text).matches()
        return false;
    }
}
