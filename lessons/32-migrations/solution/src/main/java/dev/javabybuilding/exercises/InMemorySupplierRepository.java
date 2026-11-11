package dev.javabybuilding.exercises;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Exercise 1: a SupplierRepository that keeps suppliers in a map in memory. */
public class InMemorySupplierRepository implements SupplierRepository {

    private final Map<String, Supplier> suppliers = new LinkedHashMap<>(); // key: the name in lower case

    @Override
    public void add(Supplier supplier) {
        if (suppliers.putIfAbsent(supplier.name().toLowerCase(), supplier) != null) {
            throw new IllegalArgumentException("Supplier already exists: " + supplier.name());
        }
    }

    @Override
    public Optional<Supplier> findByName(String name) {
        return Optional.ofNullable(suppliers.get(name.toLowerCase()));
    }

    @Override
    public List<Supplier> findAll() {
        return List.copyOf(suppliers.values());
    }

    @Override
    public boolean remove(String name) {
        return suppliers.remove(name.toLowerCase()) != null;
    }
}
