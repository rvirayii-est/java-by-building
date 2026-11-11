package dev.javabybuilding.inventory;

/** A product cannot be deleted because the stock log or a sale still refers to it (history must stay true). */
public class ProductInUseException extends ShopException {

    private final String productName;

    public ProductInUseException(String productName) {
        super("Cannot delete " + productName + ": it already has stock movements or sales.");
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }
}
