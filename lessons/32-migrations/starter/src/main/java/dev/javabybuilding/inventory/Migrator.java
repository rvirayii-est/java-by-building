package dev.javabybuilding.inventory;

import dev.javabybuilding.sql.SqlRunner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A tiny database migration runner, written by hand so you can see how the professional tools work (Flyway and
 * Liquibase do the same job with more features).
 *
 * <p>The idea: the database remembers which migrations it has already had, in a table called schema_version. On
 * start-up, {@link #migrate()} runs only the migrations the database has NOT had yet, oldest first, and records each
 * one. A new laptop gets V1, V2, V3; a database that is at V2 gets only V3; one that is up to date gets nothing.
 * Nobody ever runs a script by hand, and every copy of the database ends up with the same design.
 *
 * <p>Safety checks before anything runs: every migration the database has had must still exist with the SAME
 * checksum (nobody edited history), and the database must not be newer than the app.
 *
 * <p>shortcut: one app starting at a time. Flyway locks the history table so two servers cannot migrate at once.
 */
public class Migrator {

    private static final Logger LOG = Logger.getLogger(Migrator.class.getName());

    private final Database database;
    private final List<Migration> migrations;
    private final Clock clock;

    /** A runner for the given migrations (checked with {@link Migrations#validate}). */
    public Migrator(Database database, List<Migration> migrations, Clock clock) {
        this.database = database;
        this.migrations = Migrations.validate(migrations);
        this.clock = clock;
    }

    /** A runner for the migrations that come with the app (the files in src/main/resources/db/migration). */
    public static Migrator standard(Database database) {
        return new Migrator(database, Migrations.fromClasspath(), Clock.system(Inventory.SHOP_ZONE));
    }

    /**
     * Brings the database up to date.
     *
     * @return how many migrations were applied now (0 when it was already up to date)
     * @throws MigrationException when history does not match the files, or a migration fails
     */
    public int migrate() {
        createHistoryTableIfMissing();
        Map<Integer, Long> applied = appliedChecksums();
        checkHistory(applied);
        int count = 0;
        for (Migration migration : migrations) {
            if (applied.containsKey(migration.version())) {
                continue;
            }
            apply(migration);
            count++;
        }
        if (count == 0) {
            LOG.log(Level.INFO, "Database is up to date at version {0}", currentVersion());
        }
        return count;
    }

    /** The highest version applied so far, or 0 for a brand-new database. */
    public int currentVersion() {
        createHistoryTableIfMissing();
        return appliedChecksums().keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    private void createHistoryTableIfMissing() {
        try (Statement statement = connection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS schema_version ("
                    + "version INT PRIMARY KEY, description VARCHAR(200) NOT NULL, checksum BIGINT NOT NULL, applied_at TIMESTAMP NOT NULL)");
        } catch (SQLException e) {
            throw new DatabaseException("Cannot create the schema_version table", e);
        }
    }

    private Map<Integer, Long> appliedChecksums() {
        Map<Integer, Long> applied = new LinkedHashMap<>();
        try (Statement statement = connection().createStatement();
             ResultSet rows = statement.executeQuery("SELECT version, checksum FROM schema_version ORDER BY version")) {
            while (rows.next()) {
                applied.put(rows.getInt("version"), rows.getLong("checksum"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Cannot read the schema_version table", e);
        }
        return applied;
    }

    private void checkHistory(Map<Integer, Long> applied) {
        // TODO 3: for every applied (version, checksum): find the migration with that version in `migrations`.
        // None: throw new MigrationException("The database is at version N but this app only knows versions up to M. Run a newer version of the app.").
        // Found but known.checksum() differs from the stored one: throw new MigrationException("Migration V<n> (<description>) was changed after it was applied. ...").
    }

    private void apply(Migration migration) {
        // TODO 4: run the migration's script and record it, as ONE transaction: database.runInTransaction(() -> { ... }) where the
        // lambda does SqlRunner.execute(connection(), migration.script()) and recordApplied(migration) (both throw SQLException:
        // catch it inside the lambda and throw new DatabaseException("Migration V<n> failed", e)). Outside, catch DatabaseException and throw
        // new MigrationException("Migration V<n> (<description>) failed: " + e.getMessage(), e). Finally log "Applied migration V{0}: {1}".
    }

    private void recordApplied(Migration migration) throws SQLException {
        try (PreparedStatement statement = connection().prepareStatement(
                "INSERT INTO schema_version (version, description, checksum, applied_at) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, migration.version());
            statement.setString(2, migration.description());
            statement.setLong(3, migration.checksum());
            statement.setObject(4, LocalDateTime.now(clock).withNano(0));
            statement.executeUpdate();
        }
    }

    private Connection connection() {
        return database.connection();
    }
}
