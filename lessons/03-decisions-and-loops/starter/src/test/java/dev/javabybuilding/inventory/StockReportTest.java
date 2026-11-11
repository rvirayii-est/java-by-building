package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StockReportTest {

    @Test
    void reportHasAHeaderAndOneLinePerProduct() {
        String report = StockReport.build(new String[] {"Rice 25kg", "Sugar 1kg"}, new int[] {3, 0}, new int[] {5, 5});
        String expected = "Product         Qty  Status\n"
                + "Rice 25kg         3  LOW\n"
                + "Sugar 1kg         0  OUT";
        assertEquals(expected, report);
    }

    @Test
    void emptyShelfHasOnlyTheHeader() {
        assertEquals("Product         Qty  Status", StockReport.build(new String[0], new int[0], new int[0]));
    }

    @Test
    void totalValueAddsUpEveryLine() {
        assertEquals(2 * 1000 + 3 * 500, StockReport.totalValue(new int[] {1000, 500}, new int[] {2, 3}));
    }
}
