package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CounterTest {

    @Test
    void anItemNeverCountedHasCountZero() {
        assertEquals(0, new Counter<String>().count("rice"));
    }

    @Test
    void addCountsOneMoreEachTime() {
        Counter<String> counter = new Counter<>();
        counter.add("rice");
        counter.add("rice");
        counter.add("oil");
        assertEquals(2, counter.count("rice"));
        assertEquals(1, counter.count("oil"));
        assertEquals(3, counter.total());
    }

    @Test
    void addWithAnAmountAddsThatMany() {
        Counter<String> counter = new Counter<>();
        counter.add("rice", 5);
        counter.add("rice", 3);
        assertEquals(8, counter.count("rice"));
    }

    @Test
    void topGivesTheBiggestCountsFirstAndKeepsTheFirstCountedOrderOnTies() {
        Counter<String> counter = new Counter<>();
        counter.add("sugar", 2);
        counter.add("rice", 9);
        counter.add("oil", 2);
        assertEquals(List.of("rice", "sugar"), counter.top(2));
        assertEquals(List.of("rice", "sugar", "oil"), counter.top(10)); // asking for more than exist is fine
        assertEquals(List.of(), counter.top(0));
    }

    @Test
    void theSameClassCountsAnyType() {
        Counter<PaymentMethod> payments = new Counter<>();
        payments.add(PaymentMethod.GCASH);
        payments.add(PaymentMethod.GCASH);
        payments.add(PaymentMethod.CASH);
        assertEquals(List.of(PaymentMethod.GCASH, PaymentMethod.CASH), payments.top(2));
    }

    @Test
    void asMapIsACopy() {
        Counter<String> counter = new Counter<>();
        counter.add("rice");
        Map<String, Integer> copy = counter.asMap();
        copy.put("rice", 99);
        assertEquals(1, counter.count("rice"));
    }
}
