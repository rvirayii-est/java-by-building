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

    /** Text for receipts, for example "GCash". */
    public String label() {
        return label;
    }

    /** True when the cashier must write down a reference number (GCash and card payments). */
    public boolean needsReference() {
        return needsReference;
    }

    /** Finds a method by its label, ignoring capitals and spaces ("gcash" gives GCASH), or null when there is none. */
    public static PaymentMethod fromText(String text) {
        for (PaymentMethod method : values()) { // values() is every constant, in the order they are written above
            if (method.label.equalsIgnoreCase(text.trim())) {
                return method;
            }
        }
        return null;
    }
}
