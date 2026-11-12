import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Why passwords are hashed, and why a plain hash is not enough. JDK only. We use PBKDF2, a slow password hash that ships with Java,
 * to show the idea. The shop app uses BCrypt from Spring Security (same idea, same rules). Either way: NEVER invent your own scheme.
 *
 * Run:  java PasswordHashing.java
 */
public class PasswordHashing {

    static String sha256(String text) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
    }

    static String pbkdf2(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120_000, 256); // 120,000 rounds: deliberately slow
        return HexFormat.of().formatHex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());
    }

    public static void main(String[] args) throws Exception {
        // 1. A plain hash is the same for everyone with the same password, so one stolen table cracks all of them at once.
        System.out.println("sha256(admin123) twice the same?  " + sha256("admin123").equals(sha256("admin123")));
        System.out.println("sha256 length: " + sha256("admin123").length() + " hex characters, always the same for the same input");

        // 2. A salt (random bytes stored beside the hash) makes every stored hash different, even for the same password.
        SecureRandom random = new SecureRandom();
        byte[] saltA = new byte[16];
        byte[] saltB = new byte[16];
        random.nextBytes(saltA);
        random.nextBytes(saltB);
        String hashA = pbkdf2("admin123", saltA);
        String hashB = pbkdf2("admin123", saltB);
        System.out.println("pbkdf2, two salts, same password: hashes equal?  " + hashA.equals(hashB));

        // 3. Checking a login = hash the typed password with the SAME salt and compare. The password itself is never stored.
        System.out.println("right password matches?  " + MessageDigest.isEqual(hashA.getBytes(), pbkdf2("admin123", saltA).getBytes()));
        System.out.println("wrong password matches?  " + MessageDigest.isEqual(hashA.getBytes(), pbkdf2("admin124", saltA).getBytes()));

        // 4. HTTP Basic is not encryption: the header is only Base64, which anyone can undo. That is why you use HTTPS.
        String header = "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes(StandardCharsets.UTF_8));
        System.out.println(header);
        System.out.println("decoded by anyone: " + new String(Base64.getDecoder().decode(header.substring(6)), StandardCharsets.UTF_8));
    }
}
