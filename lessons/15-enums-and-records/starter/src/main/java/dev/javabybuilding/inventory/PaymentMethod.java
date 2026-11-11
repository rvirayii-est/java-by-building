package dev.javabybuilding.inventory;

/** How a customer paid. */
public enum PaymentMethod {
    CASH("Cash", false),
    GCASH("GCash", true),
    CARD("Card", true);

    private final String label;
    private final boolean needsReference;

    PaymentMethod(String label, boolean needsReference) {
        this.label = label;
        this.needsReference = needsReference;
    }

    /** TODO 2: text for receipts, for example "GCash": return the label field. */
    public String label() {
        return null;
    }

    /** TODO 2: true when the cashier must write down a reference number (GCash and card payments): return the needsReference field. */
    public boolean needsReference() {
        return false;
    }

    /**
     * TODO 2: find a method by its label, ignoring capitals and spaces ("gcash" gives GCASH), or null when there is none.
     * Loop: for (PaymentMethod method : values()) { ... }  (values() is every constant, in the order written above.)
     * Compare with method.label.equalsIgnoreCase(text.trim()).
     */
    public static PaymentMethod fromText(String text) {
        return null;
    }
}
