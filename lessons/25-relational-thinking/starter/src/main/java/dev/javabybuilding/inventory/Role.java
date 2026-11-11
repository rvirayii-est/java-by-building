package dev.javabybuilding.inventory;

/** What a staff member may do. An enum has a fixed list of values; nothing else can ever be a Role. */
public enum Role {
    CASHIER("cashier", false),
    MANAGER("manager", true);

    private final String label;
    private final boolean canVoidSales;

    Role(String label, boolean canVoidSales) { // an enum constructor runs once per value, when the program starts
        this.label = label;
        this.canVoidSales = canVoidSales;
    }

    /** Lower-case text for display, for example "cashier". */
    public String label() {
        return label;
    }

    /** Only managers may cancel a completed sale. */
    public boolean canVoidSales() {
        return canVoidSales;
    }
}
