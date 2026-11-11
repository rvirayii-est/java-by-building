package dev.javabybuilding.inventory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * One numbered step in the life of the database: a version, a description and the SQL that moves the database from
 * version-1 to this version. Stored in a file named V{version}__{description with underscores}.sql, for example
 * {@code V2__add_customer_email.sql}. (Flyway, the professional tool, uses exactly this naming rule.)
 */
public record Migration(int version, String description, String script) {

    private static final Pattern FILE_NAME = Pattern.compile("V(\\d+)__(.+)\\.sql");

    /**
     * Builds a Migration from its file name and the file's text.
     *
     * @throws MigrationException when the name does not look like V2__add_customer_email.sql
     */
    public static Migration fromFile(String fileName, String script) {
        Matcher matcher = FILE_NAME.matcher(fileName);
        if (!matcher.matches()) {
            throw new MigrationException("Not a migration file name: " + fileName + " (expected V<number>__<description>.sql)");
        }
        return new Migration(Integer.parseInt(matcher.group(1)), matcher.group(2).replace('_', ' '), script);
    }

    /**
     * A number that changes whenever the script changes (a CRC32 of its text). Line endings are made the same first, so
     * a file checked out on Windows (CRLF) and on Linux (LF) gives the same number. The database remembers the checksum
     * of every migration it applied, to notice if someone edits one afterwards.
     */
    public long checksum() {
        // TODO 1: CRC32 crc = new CRC32(); feed it the bytes of the script with every "\r\n" replaced by "\n"
        // (script.replace("\r\n", "\n").getBytes(StandardCharsets.UTF_8)); return crc.getValue().
        // Why replace the line endings? A file checked out on Windows has \r\n, on Linux \n: same migration, same checksum.
        return 0;
    }
}
