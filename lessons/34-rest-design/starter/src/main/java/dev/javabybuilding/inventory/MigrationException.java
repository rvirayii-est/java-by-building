package dev.javabybuilding.inventory;

/** The database's version history does not fit the app's migration files, or a migration failed. */
public class MigrationException extends ShopException {

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
