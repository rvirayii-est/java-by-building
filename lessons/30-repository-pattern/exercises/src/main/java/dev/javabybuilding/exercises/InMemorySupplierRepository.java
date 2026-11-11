package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Optional;

/**
 * TODO 1: a SupplierRepository that keeps suppliers in memory.
 * Hint: a LinkedHashMap<String, Supplier> keyed by the name in lower case keeps the order they were added
 * (lesson 17) and finds a name at once. putIfAbsent returns null when it stored the supplier.
 * Compare with solution/.../InMemoryProductRepository.java in the lesson 30 app.
 */
public class InMemorySupplierRepository implements SupplierRepository {

    @Override
    public void add(Supplier supplier) {
        // TODO 1a: throw new IllegalArgumentException("Supplier already exists: " + name) when the name is taken (ignoring capitals); else store it
    }

    @Override
    public Optional<Supplier> findByName(String name) {
        return Optional.empty(); // TODO 1b
    }

    @Override
    public List<Supplier> findAll() {
        return List.of(); // TODO 1c: in the order they were added
    }

    @Override
    public boolean remove(String name) {
        return false; // TODO 1d: true when a supplier was removed
    }
}
