package dev.javabybuilding.exercises;

import java.util.List;

/**
 * Lesson 25 drills: think in tables, keys and links. The first two drills answer with SQL text (the tests run your
 * SQL on a real in-memory database); the third finds the problem a flat sheet has.
 */
public class ErdDrills {

    /**
     * TODO 1: a new table for the shop's suppliers: an id that the database numbers by itself and that is the primary key,
     * a name (required, up to 60 characters) and a phone (optional, up to 20 characters).
     * Hint: look at the customer table in solution/src/main/resources/sql/schema.sql and copy its style.
     */
    public static String createSupplierTable() {
        return null;
    }

    /**
     * TODO 2: one supplier supplies many products, so the link is a column in product (the "many" side): supplier_id,
     * whole number, may be empty, and a foreign key to supplier (id).
     * Hint: ALTER TABLE product ADD COLUMN ... ; a foreign key is written REFERENCES table (column).
     */
    public static String linkProductToSupplier() {
        return null;
    }

    /**
     * TODO 3: a flat sheet repeats the customer on every row. Each row is {customer, phone}. Return the customers (each once,
     * in the order they first appear) who have MORE THAN ONE different phone number: the sheet disagrees with itself.
     * Hint: a Map from customer name to a Set of phones (a Set ignores repeats).
     */
    public static List<String> customersWithConflictingPhones(List<String[]> rows) {
        return List.of();
    }
}
