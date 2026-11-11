package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Optional;

/**
 * TODO 2: a SupplierRepository that WRAPS another one and counts how often it is used. Every method adds 1 to the
 * call counter and then does its work by calling the same method on the inner repository.
 * This is possible only because the app depends on the interface, not on one class.
 */
public class CountingSupplierRepository implements SupplierRepository {

    private final SupplierRepository inner;
    private int calls;

    public CountingSupplierRepository(SupplierRepository inner) {
        this.inner = inner;
    }

    /** How many times any of the four methods has been called. */
    public int calls() {
        return calls;
    }

    @Override
    public void add(Supplier supplier) {
        // TODO 2a: count the call, then inner.add(supplier)
    }

    @Override
    public Optional<Supplier> findByName(String name) {
        return Optional.empty(); // TODO 2b: count the call, then return what the inner repository returns
    }

    @Override
    public List<Supplier> findAll() {
        return List.of(); // TODO 2c
    }

    @Override
    public boolean remove(String name) {
        return false; // TODO 2d
    }
}
