package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Optional;

/**
 * Exercise 2: a SupplierRepository that WRAPS another one and counts how often it is used. Every method does its
 * work by calling the same method on the inner repository, after adding 1 to the call counter. This is possible
 * only because the app depends on the interface, not on one class.
 */
public class CountingSupplierRepository implements SupplierRepository {

    private final SupplierRepository inner;
    private int calls;

    public CountingSupplierRepository(SupplierRepository inner) {
        this.inner = inner;
    }

    /** How many times any method has been called. */
    public int calls() {
        return calls;
    }

    @Override
    public void add(Supplier supplier) {
        calls++;
        inner.add(supplier);
    }

    @Override
    public Optional<Supplier> findByName(String name) {
        calls++;
        return inner.findByName(name);
    }

    @Override
    public List<Supplier> findAll() {
        calls++;
        return inner.findAll();
    }

    @Override
    public boolean remove(String name) {
        calls++;
        return inner.remove(name);
    }
}
