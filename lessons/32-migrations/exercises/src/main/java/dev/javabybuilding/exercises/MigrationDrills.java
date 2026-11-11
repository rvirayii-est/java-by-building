package dev.javabybuilding.exercises;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Lesson 32 drills: the small pieces a migration runner is made of. File names look like V12__add_customer_email.sql:
 * the letter V, the version number, TWO underscores, a description with single underscores, and .sql.
 */
public class MigrationDrills {

    /** The version number in a migration file name: versionOf("V12__add_x.sql") is 12. Throws IllegalArgumentException for any other name. */
    public static int versionOf(String fileName) {
        // TODO 1: a valid name matches the regular expression "V\\d+__.+\\.sql" (fileName.matches(...)); otherwise throw
        // new IllegalArgumentException("Not a migration file name: " + fileName). The number is between the V and the first "__".
        return -1;
    }

    /** The description in a migration file name, with spaces: descriptionOf("V2__add_customer_email.sql") is "add customer email". */
    public static String descriptionOf(String fileName) {
        // TODO 2: check the name with versionOf(fileName); then take the text between the first "__" and the final ".sql"
        // (indexOf, substring) and replace('_', ' ').
        return null;
    }

    /**
     * The files still to be applied to a database that is at currentVersion: those with a HIGHER version, oldest first.
     * Sort by the version NUMBER, not by the text (V10 comes after V9, but "V10" sorts before "V9" as text).
     */
    public static List<String> pending(List<String> fileNames, int currentVersion) {
        // TODO 3: stream the names, filter versionOf(name) > currentVersion, sort by version NUMBER
        // (sorted((a, b) -> Integer.compare(versionOf(a), versionOf(b)))), toList().
        return List.of();
    }

    /** True when the versions in the file names are NOT exactly 1, 2, 3 ... n (a version is missing or used twice). Order of the list does not matter. */
    public static boolean hasGap(List<String> fileNames) {
        // TODO 4: the sorted list of versions must be exactly 1, 2, 3 ... n: compare versions.get(i) with i + 1.
        return false;
    }

    /**
     * A checksum of the script's text (CRC32 of its UTF-8 bytes), the same on Windows and Linux: first replace every
     * "\r\n" with "\n". Used to notice that an applied migration was edited.
     */
    public static long checksum(String script) {
        // TODO 5: CRC32 crc = new CRC32(); crc.update(text.getBytes(StandardCharsets.UTF_8)); return crc.getValue();
        // where text is the script with every "\r\n" replaced by "\n".
        return 0;
    }

    /**
     * The statement that creates the history table: schema_version with version (whole number, primary key),
     * description (text up to 200 characters, required) and applied_at (timestamp, required).
     */
    public static String createVersionTable() {
        // TODO 6: CREATE TABLE schema_version (...) with the three columns described above.
        return null;
    }
}
