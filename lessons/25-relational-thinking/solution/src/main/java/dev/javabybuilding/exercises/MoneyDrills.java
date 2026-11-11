package dev.javabybuilding.exercises;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Lesson 20, exercise 1: money with BigDecimal. Every answer has exactly 2 decimal places. */
public class MoneyDrills {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    /** A net price plus 12% VAT, rounded half up to the centavo: 100.00 gives 112.00; 19.99 gives 22.39. */
    public static BigDecimal addVat(BigDecimal net) {
        return net.multiply(new BigDecimal("1.12")).setScale(2, RoundingMode.HALF_UP);
    }

    /** Splits a bill between people: each pays the same amount, rounded DOWN to the centavo. 100.00 between 3 gives 33.33. */
    public static BigDecimal share(BigDecimal total, int people) {
        return total.divide(BigDecimal.valueOf(people), 2, RoundingMode.DOWN);
    }

    /** True when both amounts are worth the same, whatever their scale: 2.0 and 2.00 are the same amount. */
    public static boolean sameAmount(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    /** All the amounts added together, starting from 0.00. An empty list gives 0.00. */
    public static BigDecimal sum(List<BigDecimal> amounts) {
        BigDecimal total = ZERO;
        for (BigDecimal amount : amounts) {
            total = total.add(amount); // BigDecimal never changes: add returns a NEW number
        }
        return total;
    }

    /**
     * The change to give back: paid minus price.
     *
     * @throws IllegalArgumentException when paid is less than the price
     */
    public static BigDecimal change(BigDecimal price, BigDecimal paid) {
        if (paid.compareTo(price) < 0) {
            throw new IllegalArgumentException("Not enough money: short by " + price.subtract(paid).toPlainString());
        }
        return paid.subtract(price);
    }
}
