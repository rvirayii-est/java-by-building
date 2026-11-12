package dev.javabybuilding.exercises;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * The opposite: reads a header value back into credentials. Returns Optional.empty() when the header is null, does not start with
     * "Basic " (any other scheme, such as Bearer), is not valid Base64, or has no ':' in it. The password may itself contain ':' so
     * split at the FIRST colon only.
     */
    public static Optional<Credentials> parseBasicHeader(String header) {
        if (header == null || !header.startsWith("Basic ")) {
            return Optional.empty();
        }
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(header.substring("Basic ".length()).trim()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException notBase64) {
            return Optional.empty();
        }
        int colon = decoded.indexOf(':');
        if (colon < 0) {
            return Optional.empty();
        }
        return Optional.of(new Credentials(decoded.substring(0, colon), decoded.substring(colon + 1)));
    }

    /**
     * What is wrong with a new password? Returns the problems in this fixed order (empty list = fine):
     * "must be at least 8 characters", "must contain a letter", "must contain a digit", "must not contain spaces".
     * A null password has every problem except the spaces one.
     */
    public static List<String> passwordProblems(String password) {
        String p = password == null ? "" : password;
        List<String> problems = new ArrayList<>();
        if (p.length() < 8) {
            problems.add("must be at least 8 characters");
        }
        if (p.chars().noneMatch(Character::isLetter)) {
            problems.add("must contain a letter");
        }
        if (p.chars().noneMatch(Character::isDigit)) {
            problems.add("must contain a digit");
        }
        if (p.contains(" ")) {
            problems.add("must not contain spaces");
        }
        return problems;
    }

    /**
     * The status code of a request, decided like Spring Security does: no credentials, or wrong ones: 401 (we do not know who you are);
     * right credentials but not allowed to do this: 403 (we know you, and the answer is no); otherwise 200.
     */
    public static int statusFor(boolean sentCredentials, boolean credentialsAreValid, boolean allowed) {
        if (!sentCredentials || !credentialsAreValid) {
            return 401;
        }
        return allowed ? 200 : 403;
    }

    /** For logs: shows only the first character and hides the rest with '*', same length. mask("admin123") is "a*******"; null or "" gives "". */
    public static String mask(String secret) {
        if (secret == null || secret.isEmpty()) {
            return "";
        }
        return secret.charAt(0) + "*".repeat(secret.length() - 1);
    }

    private static final Pattern BCRYPT = Pattern.compile("\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}");

    /** Does this text look like a BCrypt hash? "$2a$10$" + 53 characters of ./A-Za-z0-9 (60 in all). null is false. */
    public static boolean looksLikeBcrypt(String text) {
        return text != null && BCRYPT.matcher(text).matches();
    }
}
