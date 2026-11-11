package dev.javabybuilding.sql;

import java.sql.Connection;
import java.sql.SQLException;

/** The shop's first real database design (lesson 28): migration V1, db/migration/V1__create_shop_tables.sql. */
public final class ShopSchema {

    /** Where the schema file lives on the class path. */
    public static final String RESOURCE = "/db/migration/V1__create_shop_tables.sql";

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
