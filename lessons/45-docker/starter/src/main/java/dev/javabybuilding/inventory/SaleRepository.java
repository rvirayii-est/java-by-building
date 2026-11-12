package dev.javabybuilding.inventory;

import java.util.List;

/** Where the shop's sales (receipts) are kept. */
public interface SaleRepository {

    /**
     * Stores a new sale and all its lines. The sale you pass is a draft: its number is ignored. The repository numbers
     * the sale itself and returns the stored sale, with that number.
     *
     * @throws DatabaseException when the sale cannot be stored (for example a line names a product that does not exist)
     */
    Sale add(Sale draft);

    /**
     * Every sale, oldest first, with its customer, lines, discount and payment. The same customer (same name and phone)
     * is ONE Customer object in all their sales, and their loyalty points are worked out again from those sales.
     */
    List<Sale> findAll();
}
