package dev.javabybuilding.inventory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Products kept in a map in memory. Nothing is saved anywhere: ideal for fast tests of the code that USES a repository. */
public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> products = new LinkedHashMap<>(); // key: the cleaned, lower-case name

    @Override
    public void add(Product product) {
        // TODO 1a: store a COPY under key(product.getName()) with putIfAbsent; when it returns non-null the name is taken:
        // throw new InvalidInputException("That product already exists: " + product.getName()).
        throw new UnsupportedOperationException("TODO 1a");
    }

    @Override
    public Optional<Product> findByName(String name) {
        // TODO 1b: products.get(key(name)), turned into an Optional of a COPY (Optional.ofNullable(...).map(...)).
        throw new UnsupportedOperationException("TODO 1b");
    }

    @Override
    public List<Product> findAll() {
        // TODO 1c: copies of all the values, in order (stream, map, toList).
        throw new UnsupportedOperationException("TODO 1c");
    }

    @Override
    public void update(Product product) {
        // TODO 1d: if the key is not in the map throw new UnknownProductException(product.getName()); otherwise replace it with a copy.
        throw new UnsupportedOperationException("TODO 1d");
    }

    private static String key(String name) {
        return TextUtil.clean(name).toLowerCase();
    }

    /** A database hands out a new object on every read, so this does too. */
    private static Product copy(Product p) {
        return new Product(p.getName(), p.getPrice(), p.getReorderLevel(), p.getQuantity());
    }
}
