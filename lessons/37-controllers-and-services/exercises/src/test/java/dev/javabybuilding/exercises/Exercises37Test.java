package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.exercises.LayerDrills.Row;
import dev.javabybuilding.exercises.LayerDrills.Sort;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class Exercises37Test {

    private static final List<Row> ROWS = List.of(
            new Row("rice", new BigDecimal("1250.50"), 12),
            new Row("Cooking Oil", new BigDecimal("98.00"), 4),
            new Row("Sugar", new BigDecimal("75.00"), 0),
            new Row("Sardines", new BigDecimal("24.00"), 60));

    @Test
    void sliceGivesOnePage() {
        List<Integer> items = IntStream.rangeClosed(1, 25).boxed().toList();
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), LayerDrills.slice(items, 1, 10));
        assertEquals(List.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20), LayerDrills.slice(items, 2, 10));
        assertEquals(List.of(21, 22, 23, 24, 25), LayerDrills.slice(items, 3, 10));
    }

    @Test
    void aPageAfterTheEndIsEmptyAndBadNumbersAreRefused() {
        List<Integer> items = List.of(1, 2, 3);
        assertTrue(LayerDrills.slice(items, 2, 3).isEmpty());
        assertTrue(LayerDrills.slice(items, 99, 10).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.slice(items, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.slice(items, 1, 0));
    }

    @Test
    void totalPagesRoundsUp() {
        assertEquals(3, LayerDrills.totalPages(25, 10));
        assertEquals(3, LayerDrills.totalPages(30, 10));
        assertEquals(4, LayerDrills.totalPages(31, 10));
        assertEquals(0, LayerDrills.totalPages(0, 10));
        assertEquals(1, LayerDrills.totalPages(1, 10));
    }

    @Test
    void parseSortReadsFieldAndDirection() {
        assertEquals(new Sort("price", false), LayerDrills.parseSort("price,desc"));
        assertEquals(new Sort("name", true), LayerDrills.parseSort("name,ASC"));
        assertEquals(new Sort("name", true), LayerDrills.parseSort("name"));
        assertEquals(new Sort("quantity", false), LayerDrills.parseSort(" quantity , Desc "));
    }

    @Test
    void parseSortRefusesBadSorts() {
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.parseSort(""));
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.parseSort(null));
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.parseSort(",desc"));
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.parseSort("price,up"));
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.parseSort("price,asc,extra"));
    }

    @Test
    void sortRowsByNameIgnoresCapitals() {
        assertEquals(List.of("Cooking Oil", "rice", "Sardines", "Sugar"), LayerDrills.sortRows(ROWS, "name").stream().map(Row::name).toList());
    }

    @Test
    void sortRowsByPriceAndQuantityBothWays() {
        assertEquals(List.of("Sardines", "Sugar", "Cooking Oil", "rice"), LayerDrills.sortRows(ROWS, "price").stream().map(Row::name).toList());
        assertEquals(List.of("rice", "Cooking Oil", "Sugar", "Sardines"), LayerDrills.sortRows(ROWS, "price,desc").stream().map(Row::name).toList());
        assertEquals(List.of("Sardines", "rice", "Cooking Oil", "Sugar"), LayerDrills.sortRows(ROWS, "quantity,desc").stream().map(Row::name).toList());
    }

    @Test
    void sortRowsRefusesAnUnknownField() {
        assertThrows(IllegalArgumentException.class, () -> LayerDrills.sortRows(ROWS, "colour"));
    }

    @Test
    void layerOfReadsTheEndOfTheName() {
        assertEquals("controller", LayerDrills.layerOf("ProductController"));
        assertEquals("service", LayerDrills.layerOf("SaleService"));
        assertEquals("repository", LayerDrills.layerOf("JdbcProductRepository"));
        assertEquals("dto", LayerDrills.layerOf("ProductResponse"));
        assertEquals("dto", LayerDrills.layerOf("CreateSaleRequest"));
        assertEquals("domain", LayerDrills.layerOf("Product"));
    }
}
