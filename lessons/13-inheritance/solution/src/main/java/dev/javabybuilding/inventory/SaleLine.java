package dev.javabybuilding.inventory;

/**
 * One line of a sale: "3 x Rice 25kg at PHP 1,250.50". It copies the name and the price at the moment of the sale,
 * so changing the product's price later does not rewrite history. Never changes after it is created.
 */
public class SaleLine {

    private final String productName;
    private final int unitPriceCentavos;
    private final int quantity;

    public SaleLine(String productName, int unitPriceCentavos, int quantity) {
        this.productName = productName;
        this.unitPriceCentavos = unitPriceCentavos;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getUnitPriceCentavos() {
        return unitPriceCentavos;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Unit price x quantity, in centavos. */
    public int lineTotal() {
        return PriceCalculator.lineTotal(unitPriceCentavos, quantity);
    }
}
