package dev.javabybuilding.security;

/**
 * The two jobs in the shop. An enum (lesson 15) because nothing else may ever be a role: a typo such as "ADMN" is a compile error
 * in the code, and an unknown word in the database fails loudly when the user is loaded.
 *
 * <p>(Not the same thing as {@code inventory.Role} of lesson 13: that one describes staff in the domain model and has a MANAGER.
 * This one describes what a login may do in the API.)
 */
public enum AppRole {
    /** Runs the shop: catalogue, deliveries, reports, and everything a cashier does. */
    ADMIN,
    /** Serves customers: reads products and sales, records sales. Nothing else. */
    CASHIER
}
