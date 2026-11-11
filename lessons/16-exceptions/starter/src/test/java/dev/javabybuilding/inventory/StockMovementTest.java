package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StockMovementTest {

    @Test
    void receivedAddsAndSoldSubtracts() {
        assertEquals(24, MovementType.RECEIVED.signedQuantity(24));
        assertEquals(-3, MovementType.SOLD.signedQuantity(3));
    }

    @Test
    void aMovementRemembersItsThreeValues() {
        StockMovement movement = new StockMovement("Sugar 1kg", MovementType.RECEIVED, 24);
        assertEquals("Sugar 1kg", movement.productName());
        assertEquals(MovementType.RECEIVED, movement.type());
        assertEquals(24, movement.quantity());
        assertEquals(24, movement.signedQuantity());
        assertEquals(-5, new StockMovement("Sugar 1kg", MovementType.SOLD, 5).signedQuantity());
    }

    @Test
    void recordsAreEqualWhenAllTheirValuesAreEqual() {
        StockMovement a = new StockMovement("Sugar 1kg", MovementType.SOLD, 2);
        StockMovement b = new StockMovement("Sugar 1kg", MovementType.SOLD, 2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new StockMovement("Sugar 1kg", MovementType.SOLD, 3));
    }

    @Test
    void aMovementOfNothingOrWithoutAProductIsRefused() {
        assertThrows(InvalidInputException.class, () -> new StockMovement("Salt", MovementType.SOLD, 0));
        assertThrows(InvalidInputException.class, () -> new StockMovement("Salt", MovementType.RECEIVED, -2));
        assertThrows(InvalidInputException.class, () -> new StockMovement(" ", MovementType.RECEIVED, 1));
    }

    @Test
    void aRecordPrintsItsValues() {
        assertEquals("StockMovement[productName=Salt, type=SOLD, quantity=1]",
                new StockMovement("Salt", MovementType.SOLD, 1).toString());
    }

    @Test
    void rolesDecideWhoMayVoidSales() {
        assertTrue(Role.MANAGER.canVoidSales());
        assertFalse(Role.CASHIER.canVoidSales());
        assertEquals("manager", Role.MANAGER.label());
        assertEquals(2, Role.values().length);
    }
}
