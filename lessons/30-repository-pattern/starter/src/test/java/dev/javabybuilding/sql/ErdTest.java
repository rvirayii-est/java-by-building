package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErdTest {

    @Test
    void theShopHasFiveTables() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            assertEquals(List.of("customer", "product", "sale", "sale_line", "stock_movement"), Erd.tables(db));
        }
    }

    @Test
    void relationshipsAreTheForeignKeys() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            assertEquals(List.of(
                    new Erd.Relationship("customer", "sale", "customer_id", true),
                    new Erd.Relationship("product", "sale_line", "product_id", false),
                    new Erd.Relationship("product", "stock_movement", "product_id", false),
                    new Erd.Relationship("sale", "sale_line", "sale_id", false)),
                    Erd.relationships(db));
        }
    }

    @Test
    void anOptionalForeignKeyIsDrawnWithAZeroOrOneEnd() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            String diagram = Erd.mermaid(db);
            assertTrue(diagram.contains("customer |o--o{ sale : customer_id"));
            assertTrue(diagram.contains("sale ||--o{ sale_line : sale_id"));
        }
    }

    @Test
    void everyTableIsListedWithItsColumnsAndKeys() throws SQLException {
        try (Connection db = PracticeDb.open()) {
            String diagram = Erd.mermaid(db);
            assertTrue(diagram.startsWith("erDiagram\n"));
            assertTrue(diagram.contains("    sale_line {\n        int id PK\n        int sale_id FK\n        int product_id FK\n"
                    + "        decimal unit_price\n        int quantity\n    }"));
        }
    }

    @Test
    void anEmptyDatabaseGivesAnEmptyDiagram() throws SQLException {
        try (Connection db = PracticeDb.openEmpty()) {
            assertEquals("erDiagram", Erd.mermaid(db));
        }
    }
}
