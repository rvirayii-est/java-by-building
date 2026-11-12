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
        if (products.putIfAbsent(key(product.getName()), copy(product)) != null) {
            throw new InvalidInputException("That product already exists: " + product.getName());
        }
    }

    @Override
    public Optional<Product> findByName(String name) {
        return Optional.ofNullable(products.get(key(name))).map(InMemoryProductRepository::copy);
    }

    @Override
    public List<Product> findAll() {
        return products.values().stream().map(InMemoryProductRepository::copy).toList();
    }

    @Override
    public void update(Product product) {
        String key = key(product.getName());
        if (!products.containsKey(key)) {
            throw new UnknownProductException(product.getName());
        }
        products.put(key, copy(product));
    }

    @Override
    public void delete(String name) {
        if (products.remove(key(name)) == null) {
            throw new UnknownProductException(name);
        }
    }

    private static String key(String name) {
        return TextUtil.clean(name).toLowerCase();
    }

    /** A database hands out a new object on every read, so this does too. */
    private static Product copy(Product p) {
        return new Product(p.getName(), p.getPrice(), p.getReorderLevel(), p.getQuantity());
    }
}
