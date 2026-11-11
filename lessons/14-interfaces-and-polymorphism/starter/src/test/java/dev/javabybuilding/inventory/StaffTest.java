package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StaffTest {

    @Test
    void staffHasARoleOnTopOfNameAndPhone() {
        Staff mara = new Staff("Mara Cruz", "0920 555 0000", "cashier");
        assertEquals("Mara Cruz", mara.getName());
        assertEquals("0920 555 0000", mara.getPhone());
        assertEquals("cashier", mara.getRole());
    }

    @Test
    void describeShowsTheRole() {
        assertEquals("Mara Cruz (cashier)", new Staff("Mara Cruz", "", "cashier").describe());
    }

    @Test
    void toStringIsInheritedFromPersonAndUsesDescribe() {
        assertEquals("Joy Lim (manager)", new Staff("Joy Lim", "", "manager").toString());
    }

    @Test
    void onlyManagersCanVoidSales() {
        assertTrue(new Staff("Joy Lim", "", "manager").canVoidSales());
        assertFalse(new Staff("Mara Cruz", "", "cashier").canVoidSales());
    }
}
