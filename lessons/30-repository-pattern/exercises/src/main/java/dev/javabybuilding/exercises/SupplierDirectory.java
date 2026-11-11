package dev.javabybuilding.exercises;

import java.util.List;

/** TODO 3: the "business" code. It may use ONLY the SupplierRepository interface, so it works with any storage. */
public class SupplierDirectory {

    private final SupplierRepository repository;

    public SupplierDirectory(SupplierRepository repository) {
        this.repository = repository;
    }

    /** TODO 3a: add the supplier unless one with this name exists already. Return true when it was added, false when it was not. */
    public boolean addIfNew(String name, String phone) {
        return false;
    }

    /** TODO 3b: the names of all suppliers, A to Z ignoring capitals (stream, map, sorted(String.CASE_INSENSITIVE_ORDER), toList). */
    public List<String> namesAToZ() {
        return List.of();
    }
}
