package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;

class DiscountTest {

    @Test
    void noDiscountTakesNothingOff() {
        assertEquals(0, new NoDiscount().discountFor(125050));
        assertEquals("No discount", new NoDiscount().label());
    }

    @Test
    void percentDiscountTakesThatPercentOff() {
        Discount tenPercent = new PercentDiscount(10);
        assertEquals(12505, tenPercent.discountFor(125050));
        assertEquals("10% off", tenPercent.label());
    }

    @Test
    void percentDiscountRoundsDownToAWholeCentavo() {
        assertEquals(199, new PercentDiscount(10).discountFor(1999));
    }

    @Test
    void percentIsKeptBetweenZeroAndOneHundred() {
        assertEquals(100, new PercentDiscount(500).getPercent());
        assertEquals(0, new PercentDiscount(-5).getPercent());
        assertEquals(1000, new PercentDiscount(500).discountFor(1000));
    }

    @Test
    void percentDiscountDoesNotOverflowOnABigSale() {
        assertEquals(100_000_000, new PercentDiscount(100).discountFor(100_000_000));
    }

    @Test
    void fixedDiscountIsACouponThatNeverGoesBelowZero() {
        Discount coupon = new FixedDiscount(5000);
        assertEquals(5000, coupon.discountFor(20000));
        assertEquals(3000, coupon.discountFor(3000));
        assertEquals("PHP 50.00 off", coupon.label());
    }

    @Test
    void seniorPwdIsTwentyPercent() {
        Discount senior = new SeniorPwdDiscount();
        assertEquals(25010, senior.discountFor(125050));
        assertEquals("Senior/PWD 20%", senior.label());
    }

    @Test
    void oneListHoldsEveryKindAndEachKnowsItsOwnAnswer() {
        List<Discount> all = List.of(new NoDiscount(), new PercentDiscount(10), new FixedDiscount(5000), new SeniorPwdDiscount());
        int[] expected = {0, 10000, 5000, 20000};
        for (int i = 0; i < all.size(); i++) {
            assertEquals(expected[i], all.get(i).discountFor(100000));
        }
    }

    @Test
    void bestPicksTheBiggestDiscountForThatSubtotal() {
        Discount coupon = new FixedDiscount(5000);
        Discount tenPercent = new PercentDiscount(10);
        List<Discount> options = List.of(coupon, tenPercent);
        assertSame(tenPercent, Discounts.best(options, 100000)); // 10000 off beats 5000 off
        assertSame(coupon, Discounts.best(options, 20000));      // 5000 off beats 2000 off
    }

    @Test
    void bestOfNothingIsNoDiscount() {
        assertEquals(0, Discounts.best(List.of(), 100000).discountFor(100000));
    }
}
