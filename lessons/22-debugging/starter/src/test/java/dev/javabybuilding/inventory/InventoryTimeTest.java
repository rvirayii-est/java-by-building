package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Times in the domain. Every test uses a Clock it controls, so nothing depends on the real date. */
class InventoryTimeTest {

    // 01:30 UTC is 09:30 in Manila (UTC+8, no daylight saving)
    private final TestClock clock = new TestClock(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);
    private final Inventory inventory = Inventory.sample(new Inventory(clock));

    @Test
    void movementsAreStampedWithTheShopLocalTime() {
        assertEquals(LocalDateTime.of(2026, 3, 1, 9, 30), inventory.movements().get(0).at());
    }

    @Test
    void aSaleIsStampedWhenItHappens() {
        clock.advance(Duration.ofHours(2).plusMinutes(15));
        Sale sale = inventory.sell("Rice 25kg", 1, Customer.WALK_IN);
        assertEquals(LocalDateTime.of(2026, 3, 1, 11, 45), sale.getDateTime());
        assertEquals("Sale #1 - 01 Mar 2026, 11:45 AM", sale.receiptHeader());
    }

    @Test
    void theStockLogIsInTimeOrder() {
        clock.advance(Duration.ofMinutes(5));
        inventory.sell("Rice 25kg", 1);
        clock.advance(Duration.ofMinutes(5));
        inventory.receive("Sugar 1kg", 10);
        List<StockMovement> log = inventory.movements();
        LocalDateTime previous = log.get(0).at();
        for (StockMovement movement : log) {
            assertTrue(!movement.at().isBefore(previous));
            previous = movement.at();
        }
        assertEquals(Duration.ofMinutes(10), Duration.between(log.get(0).at(), log.get(log.size() - 1).at()));
    }

    @Test
    void salesOnADayAreOnlyThatDaysSales() {
        inventory.sell("Sardines 155g", 10); // 1 March, 24.00 each: 240.00
        clock.advance(Duration.ofHours(8)); // 17:30, still 1 March
        inventory.sell("Sardines 155g", 5); // 120.00
        clock.advance(Duration.ofHours(8)); // 01:30 on 2 March
        inventory.sell("Sardines 155g", 1); // 24.00

        LocalDate first = LocalDate.of(2026, 3, 1);
        assertEquals(2, inventory.salesOn(first).size());
        assertEquals(Money.of("360.00"), inventory.salesTotalOn(first));
        assertEquals(Money.of("24.00"), inventory.salesTotalOn(first.plusDays(1)));
        assertEquals(Money.ZERO, inventory.salesTotalOn(first.minusDays(1)));
        assertTrue(inventory.salesOn(first.minusDays(1)).isEmpty());
    }

    @Test
    void theZoneMattersTheSameInstantIsAnotherDayInAnotherPlace() {
        // 23:30 UTC on 28 Feb is already 1 March, 07:30 in Manila.
        TestClock lateUtc = new TestClock(Instant.parse("2026-02-28T23:30:00Z"), Inventory.SHOP_ZONE);
        Inventory shop = Inventory.sample(new Inventory(lateUtc));
        assertEquals(LocalDate.of(2026, 3, 1), shop.movements().get(0).at().toLocalDate());
    }

    @Test
    void aMovementWithoutATimeIsRefused() {
        org.junit.jupiter.api.Assertions.assertThrows(InvalidInputException.class,
                () -> new StockMovement("Rice 25kg", MovementType.SOLD, 1, null));
    }
}
