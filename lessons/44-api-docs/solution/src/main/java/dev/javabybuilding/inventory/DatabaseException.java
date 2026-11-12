package dev.javabybuilding.inventory;

import java.sql.SQLException;

/**
 * Something went wrong talking to the database that is not the user's fault: the file is locked, the disk is full, a
 * rule of the schema was broken by a bug. JDBC reports these with the CHECKED SQLException, which every method would
 * have to declare. We wrap it in an unchecked ShopException (lesson 16), keep the original as the cause, and let one
 * place near the top (Main) decide what to tell the person.
 */
public class DatabaseException extends ShopException {

    /** For problems found without a SQLException, for example by the JPA repositories of lesson 38. */
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, SQLException cause) {
        super(message + " (" + cause.getMessage().lines().findFirst().orElse("") + ")");
        initCause(cause); // keep the original error and its stack trace
    }
}
