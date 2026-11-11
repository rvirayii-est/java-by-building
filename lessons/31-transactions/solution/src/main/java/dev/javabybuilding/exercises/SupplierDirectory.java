package dev.javabybuilding.exercises;

import java.util.List;

/** Exercise 3: the "business" code. It uses ONLY the SupplierRepository interface, so it works with any storage. */
public class SupplierDirectory {

    private final SupplierRepository repository;

    public SupplierDirectory(SupplierRepository repository) {
        this.repository = repository;
    }

    /** Adds the supplier unless one with this name exists already. Returns true when it was added, false when it was not. */
    public boolean addIfNew(String name, String phone) {
        if (repository.findByName(name).isPresent()) {
            return false;
        }
        repository.add(new Supplier(name, phone));
        return true;
    }

    /** The names of all suppliers, A to Z (ignoring capitals). */
    public List<String> namesAToZ() {
        return repository.findAll().stream().map(Supplier::name).sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }
}
