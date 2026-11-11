package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lesson 20, exercise 1: money with BigDecimal. Every answer has exactly 2 decimal places.
 * Remember: build from text, compare with compareTo, and say how to round (RoundingMode.HALF_UP, RoundingMode.DOWN).
 */
public class MoneyDrills {

    /**
     * TODO: a net price plus 12% VAT, rounded half up to the centavo: 100.00 gives 112.00; 19.99 gives 22.39.
     * Hint: net.multiply(new BigDecimal("1.12")).setScale(2, RoundingMode.HALF_UP)
     */
    public static BigDecimal addVat(BigDecimal net) {
        return null;
    }

    /**
     * TODO: split a bill between people: each pays the same amount, rounded DOWN to the centavo. 100.00 between 3 gives 33.33.
     * Hint: total.divide(BigDecimal.valueOf(people), 2, RoundingMode.DOWN)
     */
    public static BigDecimal share(BigDecimal total, int people) {
        return null;
    }

    /** TODO: true when both amounts are worth the same, whatever their scale: 2.0 and 2.00 are the same amount. Hint: compareTo. */
    public static boolean sameAmount(BigDecimal a, BigDecimal b) {
        return false;
    }

    /** TODO: all the amounts added together, starting from 0.00 (new BigDecimal("0.00")). An empty list gives 0.00. */
    public static BigDecimal sum(List<BigDecimal> amounts) {
        return null;
    }

    /**
     * TODO: the change to give back: paid minus price.
     * When paid is less than the price, throw new IllegalArgumentException("Not enough money: short by " + shortBy.toPlainString()).
     */
    public static BigDecimal change(BigDecimal price, BigDecimal paid) {
        return null;
    }
}
