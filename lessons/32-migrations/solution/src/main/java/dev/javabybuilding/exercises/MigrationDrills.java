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
        if (!fileName.matches("V\\d+__.+\\.sql")) {
            throw new IllegalArgumentException("Not a migration file name: " + fileName);
        }
        return Integer.parseInt(fileName.substring(1, fileName.indexOf("__")));
    }

    /** The description in a migration file name, with spaces: descriptionOf("V2__add_customer_email.sql") is "add customer email". */
    public static String descriptionOf(String fileName) {
        versionOf(fileName); // checks the name
        return fileName.substring(fileName.indexOf("__") + 2, fileName.length() - ".sql".length()).replace('_', ' ');
    }

    /**
     * The files still to be applied to a database that is at currentVersion: those with a HIGHER version, oldest first.
     * Sort by the version NUMBER, not by the text (V10 comes after V9, but "V10" sorts before "V9" as text).
     */
    public static List<String> pending(List<String> fileNames, int currentVersion) {
        return fileNames.stream()
                .filter(name -> versionOf(name) > currentVersion)
                .sorted((a, b) -> Integer.compare(versionOf(a), versionOf(b)))
                .toList();
    }

    /** True when the versions in the file names are NOT exactly 1, 2, 3 ... n (a version is missing or used twice). Order of the list does not matter. */
    public static boolean hasGap(List<String> fileNames) {
        List<Integer> versions = fileNames.stream().map(MigrationDrills::versionOf).sorted().toList();
        for (int i = 0; i < versions.size(); i++) {
            if (versions.get(i) != i + 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * A checksum of the script's text (CRC32 of its UTF-8 bytes), the same on Windows and Linux: first replace every
     * "\r\n" with "\n". Used to notice that an applied migration was edited.
     */
    public static long checksum(String script) {
        CRC32 crc = new CRC32();
        crc.update(script.replace("\r\n", "\n").getBytes(StandardCharsets.UTF_8));
        return crc.getValue();
    }

    /**
     * The statement that creates the history table: schema_version with version (whole number, primary key),
     * description (text up to 200 characters, required) and applied_at (timestamp, required).
     */
    public static String createVersionTable() {
        return "CREATE TABLE schema_version (version INT PRIMARY KEY, description VARCHAR(200) NOT NULL, applied_at TIMESTAMP NOT NULL)";
    }
}
