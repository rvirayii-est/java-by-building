package dev.javabybuilding.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

/**
 * V5 changes a table that ALREADY HAS ROWS (lesson 32's lesson: a migration must work on real data, not only on an empty database).
 * The test migrates to version 4, adds two users the way lesson 41 would have, migrates to the latest version, and looks at the roles.
 */
class UserRoleMigrationTest {

    private static final String URL = "jdbc:h2:mem:role-migration;DB_CLOSE_DELAY=-1";

    private static Flyway flyway(String target) {
        return Flyway.configure().dataSource(URL, "sa", "").target(target).load();
    }

    @Test
    void existingUsersKeepTheirLoginAndTheAdminIsPromoted() throws Exception {
        flyway("4").migrate();
        try (Connection db = DriverManager.getConnection(URL, "sa", ""); Statement sql = db.createStatement()) {
            sql.execute("INSERT INTO app_user (username, password_hash) VALUES ('admin', 'hash-a'), ('cashier', 'hash-c'), ('ana', 'hash-n')");

            flyway("latest").migrate();

            Map<String, String> roles = new LinkedHashMap<>();
            try (ResultSet rows = sql.executeQuery("SELECT username, role FROM app_user ORDER BY username")) {
                while (rows.next()) {
                    roles.put(rows.getString("username"), rows.getString("role"));
                }
            }
            assertEquals(Map.of("admin", "ADMIN", "ana", "CASHIER", "cashier", "CASHIER"), roles);
        }
    }
}
