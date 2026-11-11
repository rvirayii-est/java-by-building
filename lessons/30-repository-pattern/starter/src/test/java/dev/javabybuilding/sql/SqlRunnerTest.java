package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class SqlRunnerTest {

    @Test
    void splitCutsAtSemicolons() {
        assertEquals(List.of("SELECT 1", "SELECT 2"), SqlRunner.split("SELECT 1; SELECT 2;"));
    }

    @Test
    void splitIgnoresASemicolonInsideQuotes() {
        assertEquals(List.of("SELECT 'a;b'"), SqlRunner.split("SELECT 'a;b';"));
    }

    @Test
    void splitDropsCommentsAndBlankStatements() {
        assertEquals(List.of("SELECT 1"), SqlRunner.split("-- a comment; with a semicolon\nSELECT 1;\n;\n  \n"));
    }

    @Test
    void aDoubledQuoteInsideTextDoesNotEndTheText() {
        assertEquals(List.of("SELECT 'it''s; fine'"), SqlRunner.split("SELECT 'it''s; fine'"));
    }

    @Test
    void runGivesTheRowsOfTheLastSelect() throws SQLException {
        try (Connection db = PracticeDb.openEmpty()) {
            QueryResult result = SqlRunner.run(db, "CREATE TABLE t (n INT); INSERT INTO t VALUES (1), (2); SELECT n FROM t ORDER BY n");
            assertTrue(result.isQuery());
            assertEquals(List.of("n"), result.columns());
            assertEquals(List.of("1", "2"), result.column("n"));
        }
    }

    @Test
    void runGivesTheUpdateCountOfAnInsertOrUpdate() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            QueryResult result = SqlRunner.run(db, "UPDATE product SET price = price + 1 WHERE quantity < 10");
            assertFalse(result.isQuery());
            assertEquals(3, result.updateCount());
            assertEquals("(3 rows updated)", result.toTable());
        }
    }

    @Test
    void anEmptyScriptChangesNothing() throws SQLException {
        try (Connection db = PracticeDb.openEmpty()) {
            assertEquals(0, SqlRunner.run(db, "-- nothing here").updateCount());
        }
    }

    @Test
    void nullBecomesTheWordNull() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            assertEquals(List.of("NULL", "NULL"), SqlRunner.query(db, "SELECT customer_id FROM sale WHERE customer_id IS NULL").column("customer_id"));
        }
    }

    @Test
    void queryRefusesAnythingButASelect() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            assertThrows(IllegalArgumentException.class, () -> SqlRunner.query(db, "DELETE FROM stock_movement"));
        }
    }

    @Test
    void badSqlThrowsAnSqlException() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            assertThrows(SQLException.class, () -> SqlRunner.query(db, "SELEKT * FROM product"));
        }
    }
}
