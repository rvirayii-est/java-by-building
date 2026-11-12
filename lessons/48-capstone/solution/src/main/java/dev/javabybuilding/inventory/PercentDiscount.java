package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Takes a percentage (0 to 100) off, rounded DOWN to a whole centavo (as it always was: int division dropped the rest). */
public class PercentDiscount implements Discount {

    private final int percent;

    /** @throws InvalidInputException unless percent is from 0 to 100 */
    public PercentDiscount(int percent) {
        if (percent < 0 || percent > 100) {
            throw new InvalidInputException("A percentage must be from 0 to 100.");
        }
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    @Override
    public BigDecimal discountFor(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
    }

    @Override
    public String label() {
        return percent + "% off";
    }
}
