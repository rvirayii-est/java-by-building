package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StockUpdaterTest {

    private final String[] names = {"Rice 25kg", "Sugar 1kg"};

    @Test
    void indexOfFindsThePosition() {
        assertEquals(0, StockUpdater.indexOf(names, "Rice 25kg"));
        assertEquals(1, StockUpdater.indexOf(names, "Sugar 1kg"));
    }

    @Test
    void indexOfIgnoresCapitalsAndExtraSpaces() {
        assertEquals(1, StockUpdater.indexOf(names, "  sugar   1KG "));
    }

    @Test
    void indexOfIsMinusOneWhenMissing() {
        assertEquals(-1, StockUpdater.indexOf(names, "Salt"));
    }

    @Test
    void receiveChangesTheArrayThatWasPassedIn() {
        int[] quantities = {5, 0};
        StockUpdater.receive(quantities, 1, 24);
        assertArrayEquals(new int[] {5, 24}, quantities);
    }

    @Test
    void sellTakesStockOff() {
        int[] quantities = {5, 0};
        assertTrue(StockUpdater.sell(quantities, 0, 3));
        assertArrayEquals(new int[] {2, 0}, quantities);
    }

    @Test
    void sellRefusesWhenThereIsNotEnoughAndChangesNothing() {
        int[] quantities = {5, 0};
        assertFalse(StockUpdater.sell(quantities, 0, 6));
        assertArrayEquals(new int[] {5, 0}, quantities);
    }

    @Test
    void sellingExactlyWhatIsLeftIsAllowed() {
        int[] quantities = {5};
        assertTrue(StockUpdater.sell(quantities, 0, 5));
        assertEquals(0, quantities[0]);
    }
}
