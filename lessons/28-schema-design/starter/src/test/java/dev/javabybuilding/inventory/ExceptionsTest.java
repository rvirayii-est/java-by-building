package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExceptionsTest {

    @Test
    void everyShopProblemIsAShopExceptionAndAnUncheckedRuntimeException() {
        assertTrue(ShopException.class.isAssignableFrom(InvalidInputException.class));
        assertTrue(ShopException.class.isAssignableFrom(UnknownProductException.class));
        assertTrue(ShopException.class.isAssignableFrom(InsufficientStockException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(ShopException.class));
    }

    @Test
    void oneCatchBlockCanHandleEveryKindAndGetsTheCashierFriendlyMessage() {
        Inventory inventory = Inventory.sample();
        List<Runnable> badActions = List.of(
                () -> inventory.sell("Salt", 1),
                () -> inventory.sell("Rice 25kg", 500),
                () -> inventory.receive("Rice 25kg", 0),
                () -> inventory.addProduct("Rice 25kg", Money.of("1.00"), 1));
        List<String> messages = new ArrayList<>();
        for (Runnable action : badActions) {
            try {
                action.run();
            } catch (ShopException e) {
                messages.add(e.getMessage());
            }
        }
        assertEquals(List.of(
                "Unknown product: Salt",
                "Not enough stock: only 12 of Rice 25kg left.",
                "Quantity must be at least 1.",
                "That product already exists: Rice 25kg"), messages);
    }

    @Test
    void aFailedSaleCanBeRetriedAfterRestocking() {
        Inventory inventory = Inventory.sample();
        assertThrows(InsufficientStockException.class, () -> inventory.sell("Sugar 1kg", 3)); // sugar is out
        inventory.receive("Sugar 1kg", 10);
        assertEquals(Money.of("225.00"), inventory.sell("Sugar 1kg", 3));
        assertEquals(7, inventory.quantityOf("Sugar 1kg"));
    }

    @Test
    void anExceptionKeepsTheMessageItWasBuiltWith() {
        ShopException e = new ShopException("custom text");
        assertEquals("custom text", e.getMessage());
    }
}
