package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises38Test {

    @Test
    void snakeTurnsCamelCaseIntoSnakeCase() {
        assertEquals("reorder_level", MappingDrills.snake("reorderLevel"));
        assertEquals("sold_at", MappingDrills.snake("soldAt"));
        assertEquals("sale_line", MappingDrills.snake("SaleLine"));
        assertEquals("name", MappingDrills.snake("name"));
        assertEquals("unit_price_php", MappingDrills.snake("unitPricePhp"));
    }

    @Test
    void selectSqlListsTheColumns() {
        assertEquals("SELECT name, price FROM product", MappingDrills.selectSql("product", List.of("name", "price")));
        assertEquals("SELECT id FROM sale", MappingDrills.selectSql("sale", List.of("id")));
    }

    @Test
    void insertSqlHasOneQuestionMarkPerColumn() {
        assertEquals("INSERT INTO product (name, price, reorder_level) VALUES (?, ?, ?)",
                MappingDrills.insertSql("product", List.of("name", "price", "reorder_level")));
        assertEquals("INSERT INTO customer (name) VALUES (?)", MappingDrills.insertSql("customer", List.of("name")));
    }

    @Test
    void noColumnsIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> MappingDrills.selectSql("product", List.of()));
        assertThrows(IllegalArgumentException.class, () -> MappingDrills.insertSql("product", List.of()));
    }

    @Test
    void derivedWhereReadsOneOrMoreFields() {
        assertEquals("WHERE name = ?", MappingDrills.derivedWhere("findByName"));
        assertEquals("WHERE name = ? AND phone = ?", MappingDrills.derivedWhere("findByNameAndPhone"));
        assertEquals("WHERE reorder_level = ?", MappingDrills.derivedWhere("findByReorderLevel"));
    }

    @Test
    void derivedWhereUnderstandsIgnoreCase() {
        assertEquals("WHERE UPPER(name) = UPPER(?)", MappingDrills.derivedWhere("findByNameIgnoreCase"));
        assertEquals("WHERE UPPER(name) = UPPER(?) AND phone = ?", MappingDrills.derivedWhere("findByNameIgnoreCaseAndPhone"));
    }

    @Test
    void derivedWhereRefusesOtherMethodNames() {
        assertThrows(IllegalArgumentException.class, () -> MappingDrills.derivedWhere("deleteAll"));
        assertThrows(IllegalArgumentException.class, () -> MappingDrills.derivedWhere("findBy"));
        assertThrows(IllegalArgumentException.class, () -> MappingDrills.derivedWhere("findByAndPhone"));
    }

    @Test
    void tableForDropsTheEntitySuffix() {
        assertEquals("sale_line", MappingDrills.tableFor("SaleLineEntity"));
        assertEquals("product", MappingDrills.tableFor("ProductEntity"));
        assertEquals("customer", MappingDrills.tableFor("Customer"));
    }
}
