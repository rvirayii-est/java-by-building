package dev.javabybuilding.sql;

import java.sql.Connection;
import java.sql.SQLException;

/** The shop's real database design: the file db/schema.sql in src/main/resources. */
public final class ShopSchema {

    /** Where the schema file lives on the class path. */
    public static final String RESOURCE = "/db/schema.sql";

    private ShopSchema() {}

    /** The text of the schema file. */
    public static String script() {
        return PracticeDb.resource(RESOURCE);
    }

    /** Creates all the shop's tables, constraints and indexes in an empty database. */
    public static void create(Connection connection) throws SQLException {
        SqlRunner.execute(connection, script());
    }
}
