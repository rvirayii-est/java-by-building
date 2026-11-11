package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * YOUR tests. Work like this (red, green, refactor):
 *
 *   1. Pick ONE rule from the specification in BulkPricing.java.
 *   2. Replace the matching fail(...) below with a real test (Arrange, Act, Assert). Run it: it must FAIL for the right reason (red).
 *   3. Write the least code in BulkPricing that makes it pass (green). Run ALL tests.
 *   4. Clean up the code without changing behaviour; run the tests again (refactor).
 *   5. Next rule.
 *
 * Replace each fail(...) with a real test. You may rename the methods, merge them into one @ParameterizedTest
 * (@CsvSource) or add more. Do not delete a rule without testing it somewhere.
 */
class BulkPricingTest {

    @Test
    void todo1_noDiscountForOneToNineUnits() {
        fail("TODO 1: test discountPercent(1) and discountPercent(9)");
    }

    @Test
    void todo2_fivePercentFromTenToFortyNineUnits() {
        fail("TODO 2: test discountPercent(10) and discountPercent(49)");
    }

    @Test
    void todo3_tenPercentFromFiftyToNinetyNineUnits() {
        fail("TODO 3: test discountPercent(50) and discountPercent(99)");
    }

    @Test
    void todo4_fifteenPercentFromOneHundredUnits() {
        fail("TODO 4: test discountPercent(100) and a big number");
    }

    @Test
    void todo5_zeroOrNegativeQuantityIsRefused() {
        fail("TODO 5: use assertThrows(IllegalArgumentException.class, ...) and check the message");
    }

    @Test
    void todo6_lineTotalAppliesTheDiscountAndRoundsHalfUp() {
        fail("TODO 6: test lineTotal with and without a discount, and one price where rounding matters (0.15 x 10)");
    }
}
