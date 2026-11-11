package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PersonTest {

    @Test
    void aPersonRemembersNameAndPhone() {
        Person ben = new Person("Ben Cruz", "0918 000 1111");
        assertEquals("Ben Cruz", ben.getName());
        assertEquals("0918 000 1111", ben.getPhone());
    }

    @Test
    void describeIsJustTheNameAndToStringUsesIt() {
        Person ben = new Person("Ben Cruz", "");
        assertEquals("Ben Cruz", ben.describe());
        assertEquals("Ben Cruz", ben.toString());
    }

    @Test
    void subclassesInheritNameAndPhoneFromPerson() {
        Customer ana = new Customer("Ana Reyes", "0917 123 4567");
        Staff mara = new Staff("Mara Cruz", "0920 555 0000", Role.CASHIER);
        assertEquals("0917 123 4567", ana.getPhone());
        assertEquals("Mara Cruz", mara.getName());
    }

    @Test
    void aCustomerAndAStaffAreBothPeople() {
        Person first = new Customer("Ana", "");
        Person second = new Staff("Mara", "", Role.CASHIER);
        assertTrue(first instanceof Customer);
        assertFalse(first instanceof Staff);
        assertTrue(second instanceof Staff);
    }

    @Test
    void eachKindOfPersonDescribesItselfDifferently() {
        Customer ana = new Customer("Ana Reyes", "");
        ana.addPurchase(Money.of("300.00"));
        Person asPerson = ana; // the variable says Person, but the object is still a Customer
        assertEquals("Ana Reyes (3 points)", asPerson.describe());
        assertEquals("Mara Cruz (cashier)", new Staff("Mara Cruz", "", Role.CASHIER).describe());
    }
}
