package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StaffTest {

    @Test
    void staffHasARoleOnTopOfNameAndPhone() {
        Staff mara = new Staff("Mara Cruz", "0920 555 0000", Role.CASHIER);
        assertEquals("Mara Cruz", mara.getName());
        assertEquals("0920 555 0000", mara.getPhone());
        assertEquals(Role.CASHIER, mara.getRole());
    }

    @Test
    void describeShowsTheRole() {
        assertEquals("Mara Cruz (cashier)", new Staff("Mara Cruz", "", Role.CASHIER).describe());
    }

    @Test
    void toStringIsInheritedFromPersonAndUsesDescribe() {
        assertEquals("Joy Lim (manager)", new Staff("Joy Lim", "", Role.MANAGER).toString());
    }

    @Test
    void onlyManagersCanVoidSales() {
        assertTrue(new Staff("Joy Lim", "", Role.MANAGER).canVoidSales());
        assertFalse(new Staff("Mara Cruz", "", Role.CASHIER).canVoidSales());
    }
}
