package dev.javabybuilding.inventory;

import java.math.BigDecimal;

/**
 * One line of a sale: "3 x Rice 25kg at PHP 1,250.50". It copies the name and the price at the moment of the sale,
 * so changing the product's price later does not rewrite history.
 */
public record SaleLine(String productName, BigDecimal unitPrice, int quantity) {

    /** Unit price x quantity. */
    public BigDecimal lineTotal() {
        return PriceCalculator.lineTotal(unitPrice, quantity);
    }
}
