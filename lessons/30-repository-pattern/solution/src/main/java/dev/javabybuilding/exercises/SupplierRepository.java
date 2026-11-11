package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Optional;

/** Where suppliers are kept. Lesson 30 exercise: the interface is given; you write what stands behind it. */
public interface SupplierRepository {

    /**
     * Stores a supplier.
     *
     * @throws IllegalArgumentException when a supplier with this name (ignoring capitals) is already stored
     */
    void add(Supplier supplier);

    /** The supplier with this name, ignoring capitals, or empty. */
    Optional<Supplier> findByName(String name);

    /** Every supplier, in the order they were added. */
    List<Supplier> findAll();

    /** Removes the supplier with this name (ignoring capitals). True when one was removed, false when there was none. */
    boolean remove(String name);
}
