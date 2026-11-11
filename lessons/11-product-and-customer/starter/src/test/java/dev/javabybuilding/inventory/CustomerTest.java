package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void aNewCustomerRemembersNameAndPhoneAndHasNoPoints() {
        Customer ana = new Customer("Ana Reyes", "0917 123 4567");
        assertEquals("Ana Reyes", ana.getName());
        assertEquals("0917 123 4567", ana.getPhone());
        assertEquals(0, ana.getPoints());
    }

    @Test
    void oneFullHundredPesosEarnsOnePoint() {
        Customer ana = new Customer("Ana", "");
        assertEquals(0, ana.pointsFor(9999));
        assertEquals(1, ana.pointsFor(10000));
        assertEquals(12, ana.pointsFor(125050));
    }

    @Test
    void addPurchaseAddsThePointsUp() {
        Customer ana = new Customer("Ana", "");
        ana.addPurchase(125050);
        ana.addPurchase(5000);
        assertEquals(12, ana.getPoints());
    }

    @Test
    void toStringShowsNameAndPoints() {
        Customer ana = new Customer("Ana Reyes", "");
        ana.addPurchase(30000);
        assertEquals("Ana Reyes (3 points)", ana.toString());
    }
}
