package dev.javabybuilding.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Finds the app's migration files and checks that the list makes sense. */
public final class Migrations {

    /** The folder inside src/main/resources that holds the migration files. */
    public static final String FOLDER = "/db/migration/";

    private Migrations() {}

    /**
     * The migrations that come with the app, oldest first. The file index.txt in the same folder lists their names, one
     * per line: a classpath cannot always be searched for files (inside a jar it cannot, simply), so we ask for a list.
     * Flyway searches the folder by itself; a test here checks that index.txt does not forget a file.
     */
    public static List<Migration> fromClasspath() {
        List<Migration> migrations = new ArrayList<>();
        for (String name : read("index.txt").split("\\R")) {
            if (!name.isBlank()) {
                migrations.add(Migration.fromFile(name.strip(), read(name.strip())));
            }
        }
        return validate(migrations);
    }

    /**
     * The same migrations sorted by version, after checking that the versions are 1, 2, 3 ... with no gap and no
     * duplicate. (A gap usually means a file is missing; a duplicate usually means two people picked the same number.)
     *
     * @throws MigrationException when the list is not valid
     */
    public static List<Migration> validate(List<Migration> migrations) {
        // TODO 2: sort a copy by version, then walk it: the version at position i must be i + 1. Throw a MigrationException
        // "Two migrations have the version N" when two neighbours have the same version, and "Migration V<i+1> is missing
        // (found V<N> instead)" when the number is not the expected one. Return the sorted list.
        return new ArrayList<>(migrations); // as it is, no checking and no sorting
    }

    private static String read(String fileName) {
        try (InputStream in = Migrations.class.getResourceAsStream(FOLDER + fileName)) {
            if (in == null) {
                throw new MigrationException("Migration file not found: " + FOLDER + fileName);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MigrationException("Cannot read " + FOLDER + fileName, e);
        }
    }
}
