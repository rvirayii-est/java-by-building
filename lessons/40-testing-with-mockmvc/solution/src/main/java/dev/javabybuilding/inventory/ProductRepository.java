package dev.javabybuilding.inventory;

import java.util.List;
import java.util.Optional;

/**
 * Where the shop's products are kept, as the rest of the app sees it: a collection you can add to, search and update.
 * Nothing here says "SQL", "file" or "memory". Three classes implement it (JdbcProductRepository,
 * FileProductRepository, InMemoryProductRepository) and ProductRepositoryContract makes sure they all behave the same.
 *
 * <p>The objects you get back are COPIES of what is stored: change one and nothing is saved until you call
 * {@link #update(Product)}. That is how a database behaves, and the in-memory version imitates it on purpose.
 */
public interface ProductRepository {

    /**
     * Stores a new product.
     *
     * @throws InvalidInputException when a product with this name already exists (ignoring capitals and spaces)
     */
    void add(Product product);

    /** The product with this name (ignoring capitals and extra spaces), or empty when there is none. */
    Optional<Product> findByName(String name);

    /** Every product, in the order they were added. */
    List<Product> findAll();

    /**
     * Saves the product's price, reorder level and quantity; the name says which one.
     *
     * @throws UnknownProductException when no product has this name
     */
    void update(Product product);

    /**
     * Removes the product with this name (lesson 34: the API can delete products). The caller decides whether that is
     * allowed; a repository only does it.
     *
     * @throws UnknownProductException when no product has this name
     */
    void delete(String name);
}
