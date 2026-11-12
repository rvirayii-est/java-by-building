package dev.javabybuilding.inventory;

/** There is no product with that name. */
public class UnknownProductException extends ShopException {

    private final String productName;

    public UnknownProductException(String productName) {
        super("Unknown product: " + productName);
        this.productName = productName;
    }

    /** The name that was asked for, as typed. */
    public String getProductName() {
        return productName;
    }
}
