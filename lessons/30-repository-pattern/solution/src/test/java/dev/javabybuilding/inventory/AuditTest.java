package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** audit() is a tool for finding bugs: it must say "all good" for a healthy shop and name the product when not. */
class AuditTest {

    @Test
    void aShopThatOnlyUsesItsOwnMethodsAlwaysPassesTheAudit() {
        Inventory inventory = Inventory.sample();
        inventory.sell("Rice 25kg", 5);
        inventory.receive("Sugar 1kg", 30);
        inventory.sell("Sugar 1kg", 10);
        assertTrue(inventory.audit().isEmpty());
    }

    @Test
    void aShelfThatDisagreesWithTheLogIsNamed() {
        // Built by hand: 99 on the shelf, but the log only ever received 12.
        Product rice = new Product("Rice 25kg", Money.of("1250.50"), 10, 99);
        StockMovement received = new StockMovement("Rice 25kg", MovementType.RECEIVED, 12, LocalDateTime.of(2026, 3, 1, 9, 30));
        Inventory broken = Inventory.restore(List.of(rice), List.of(), List.of(received), java.time.Clock.systemDefaultZone());

        assertEquals(List.of("Rice 25kg: the shelf has 99 but the stock log adds up to 12"), broken.audit());
    }

    @Test
    void anEmptyShopPassesTheAudit() {
        assertTrue(new Inventory().audit().isEmpty());
    }
}
