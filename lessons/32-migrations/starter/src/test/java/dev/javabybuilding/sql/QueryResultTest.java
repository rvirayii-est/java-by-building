package dev.javabybuilding.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class QueryResultTest {

    private final QueryResult sample = QueryResult.ofRows(List.of("name", "qty"),
            List.of(List.of("Rice 25kg", "17"), List.of("Sugar 1kg", "4")));

    @Test
    void toTableAlignsTheColumns() {
        assertEquals("name      | qty\n"
                + "----------+----\n"
                + "Rice 25kg | 17\n"
                + "Sugar 1kg | 4\n"
                + "(2 rows)", sample.toTable());
    }

    @Test
    void aTableWithNoRowsStillShowsTheHeader() {
        assertEquals("n\n-\n(0 rows)", QueryResult.ofRows(List.of("n"), List.of()).toTable());
    }

    @Test
    void oneRowIsSingular() {
        assertEquals("n\n-\n7\n(1 row)", QueryResult.ofRows(List.of("n"), List.of(List.of("7"))).toTable());
        assertEquals("(1 row updated)", QueryResult.ofUpdate(1).toTable());
    }

    @Test
    void cellAndColumnReadValues() {
        assertEquals("Sugar 1kg", sample.cell(1, 0));
        assertEquals(List.of("17", "4"), sample.column("QTY"));
        assertEquals(2, sample.rowCount());
    }

    @Test
    void anUnknownColumnIsAnError() {
        assertThrows(IllegalArgumentException.class, () -> sample.column("price"));
    }
}
