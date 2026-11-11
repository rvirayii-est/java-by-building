package dev.javabybuilding.exercises;

/**
 * Lesson 28 drills: design the supplier side of the shop. Each method returns ONE SQL statement. The tests run them
 * in order, on a database that already has the table product (id, name, price), and then try to break your rules.
 */
public class SchemaDrills {

    /**
     * Table supplier: id (self-numbered primary key), name (required, up to 60 characters, UNIQUE, and not blank:
     * TRIM(name) <> ''), phone (optional, up to 20 characters). Give the two rules names starting uq_ and ck_.
     */
    public static String createSupplier() {
        return null; // TODO 1
    }

    /**
     * Give product an optional supplier_id that is a foreign key to supplier (id). If a supplier is deleted, the
     * products stay and their supplier_id becomes NULL (ON DELETE SET NULL).
     */
    public static String linkProductToSupplier() {
        return null; // TODO 2
    }

    /**
     * Table delivery: one row per product delivered. Columns: id (self-numbered primary key), supplier_id and
     * product_id (both required, both foreign keys, the default RESTRICT is right: history must not lose its supplier
     * or product), quantity (required, must be more than 0) and received_at (required timestamp).
     */
    public static String createDelivery() {
        return null; // TODO 3
    }

    /** The owner often asks "what arrived this week?": an index named idx_delivery_received_at on delivery (received_at). */
    public static String indexDeliveryDate() {
        return null; // TODO 4
    }
}
