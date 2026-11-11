package dev.javabybuilding.inventory;

/**
 * One line of a sale: "3 x Rice 25kg at PHP 1,250.50". It copies the name and the price at the moment of the sale,
 * so changing the product's price later does not rewrite history.
 * In lesson 11 this was a class with a constructor and three getters; it only holds data, so it is now a record.
 * The getters became accessors without "get": line.productName() instead of line.getProductName().
 */
public record SaleLine(String productName, int unitPriceCentavos, int quantity) {

    /** Unit price x quantity, in centavos. */
    public int lineTotal() {
        return PriceCalculator.lineTotal(unitPriceCentavos, quantity);
    }
}
