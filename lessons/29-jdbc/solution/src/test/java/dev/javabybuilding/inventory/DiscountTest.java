package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class DiscountTest {

    @Test
    void noDiscountTakesNothingOff() {
        assertEquals(Money.ZERO, new NoDiscount().discountFor(Money.of("1250.50")));
        assertEquals("No discount", new NoDiscount().label());
    }

    @Test
    void percentDiscountTakesThatPercentOff() {
        Discount tenPercent = new PercentDiscount(10);
        assertEquals(Money.of("125.05"), tenPercent.discountFor(Money.of("1250.50")));
        assertEquals("10% off", tenPercent.label());
    }

    @Test
    void percentDiscountRoundsDownToAWholeCentavo() {
        assertEquals(Money.of("1.99"), new PercentDiscount(10).discountFor(Money.of("19.99"))); // 1.999 is cut, not rounded up
    }

    @Test
    void percentOutsideZeroToOneHundredIsRefused() {
        assertThrows(InvalidInputException.class, () -> new PercentDiscount(101));
        assertThrows(InvalidInputException.class, () -> new PercentDiscount(-1));
        assertEquals(0, new PercentDiscount(0).getPercent());
        assertEquals(100, new PercentDiscount(100).getPercent());
    }

    @Test
    void aNegativeCouponIsRefused() {
        assertThrows(InvalidInputException.class, () -> new FixedDiscount(Money.of("-0.01")));
    }

    @Test
    void percentDiscountHasNoSizeLimitOnABigSale() {
        assertEquals(Money.of("1000000000.00"), new PercentDiscount(100).discountFor(Money.of("1000000000.00")));
    }

    @Test
    void fixedDiscountIsACouponThatNeverGoesBelowZero() {
        Discount coupon = new FixedDiscount(Money.of("50.00"));
        assertEquals(Money.of("50.00"), coupon.discountFor(Money.of("200.00")));
        assertEquals(Money.of("30.00"), coupon.discountFor(Money.of("30.00")));
        assertEquals("PHP 50.00 off", coupon.label());
    }

    @Test
    void seniorPwdIsTwentyPercent() {
        Discount senior = new SeniorPwdDiscount();
        assertEquals(Money.of("250.10"), senior.discountFor(Money.of("1250.50")));
        assertEquals("Senior/PWD 20%", senior.label());
    }

    @Test
    void oneListHoldsEveryKindAndEachKnowsItsOwnAnswer() {
        List<Discount> all = List.of(new NoDiscount(), new PercentDiscount(10), new FixedDiscount(Money.of("50.00")), new SeniorPwdDiscount());
        String[] expected = {"0.00", "100.00", "50.00", "200.00"};
        for (int i = 0; i < all.size(); i++) {
            assertEquals(Money.of(expected[i]), all.get(i).discountFor(Money.of("1000.00")));
        }
    }

    @Test
    void bestPicksTheBiggestDiscountForThatSubtotal() {
        Discount coupon = new FixedDiscount(Money.of("50.00"));
        Discount tenPercent = new PercentDiscount(10);
        List<Discount> options = List.of(coupon, tenPercent);
        assertSame(tenPercent, Discounts.best(options, Money.of("1000.00"))); // 100.00 off beats 50.00 off
        assertSame(coupon, Discounts.best(options, Money.of("200.00")));      // 50.00 off beats 20.00 off
    }

    @Test
    void bestOfNothingIsNoDiscount() {
        assertEquals(Money.ZERO, Discounts.best(List.of(), Money.of("1000.00")).discountFor(Money.of("1000.00")));
    }
}
