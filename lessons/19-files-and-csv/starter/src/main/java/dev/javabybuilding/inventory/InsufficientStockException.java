package dev.javabybuilding.inventory;

/** A sale asked for more units than the shelf has. Carries the numbers so the caller can say something useful. */
public class InsufficientStockException extends ShopException {

    private final String productName;
    private final int requested;
    private final int available;

    public InsufficientStockException(String productName, int requested, int available) {
        super("Not enough stock: only " + available + " of " + productName + " left.");
        this.productName = productName;
        this.requested = requested;
        this.available = available;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
