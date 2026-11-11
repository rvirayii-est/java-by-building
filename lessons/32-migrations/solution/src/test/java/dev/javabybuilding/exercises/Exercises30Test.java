package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class Exercises30Test {

    /** The same rules for every SupplierRepository: the plain one, and the counting wrapper around it. */
    static Stream<Supplier<SupplierRepository>> repositories() {
        return Stream.of(InMemorySupplierRepository::new, () -> new CountingSupplierRepository(new InMemorySupplierRepository()));
    }

    private static dev.javabybuilding.exercises.Supplier metro() {
        return new dev.javabybuilding.exercises.Supplier("Metro Trading", "0917");
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void aStoredSupplierCanBeFoundIgnoringCapitals(Supplier<SupplierRepository> create) {
        SupplierRepository repository = create.get();
        repository.add(metro());
        assertEquals(metro(), repository.findByName("METRO trading").orElseThrow());
        assertTrue(repository.findByName("Gold Star").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void theSameNameTwiceIsRefused(Supplier<SupplierRepository> create) {
        SupplierRepository repository = create.get();
        repository.add(metro());
        assertThrows(IllegalArgumentException.class, () -> repository.add(new dev.javabybuilding.exercises.Supplier("metro TRADING", "0999")));
        assertEquals(1, repository.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void findAllKeepsTheOrderTheyWereAdded(Supplier<SupplierRepository> create) {
        SupplierRepository repository = create.get();
        repository.add(new dev.javabybuilding.exercises.Supplier("Gold Star", "1"));
        repository.add(metro());
        assertEquals(List.of("Gold Star", "Metro Trading"), repository.findAll().stream().map(dev.javabybuilding.exercises.Supplier::name).toList());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void removeSaysWhetherItRemovedAnything(Supplier<SupplierRepository> create) {
        SupplierRepository repository = create.get();
        repository.add(metro());
        assertTrue(repository.remove("metro trading"));
        assertFalse(repository.remove("metro trading"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void theCountingRepositoryCountsEveryCall() {
        CountingSupplierRepository counting = new CountingSupplierRepository(new InMemorySupplierRepository());
        counting.add(metro());
        counting.findByName("metro trading");
        counting.findAll();
        counting.remove("metro trading");
        assertEquals(4, counting.calls());
    }

    @Test
    void theCountingRepositoryReallyStoresInTheInnerOne() {
        InMemorySupplierRepository inner = new InMemorySupplierRepository();
        new CountingSupplierRepository(inner).add(metro());
        assertEquals(1, inner.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("repositories")
    void addIfNewAddsOnlyOnce(Supplier<SupplierRepository> create) {
        SupplierDirectory directory = new SupplierDirectory(create.get());
        assertTrue(directory.addIfNew("Metro Trading", "0917"));
        assertFalse(directory.addIfNew("metro trading", "0999"));
        assertEquals(List.of("Metro Trading"), directory.namesAToZ());
    }

    @Test
    void namesAreSortedIgnoringCapitals() {
        SupplierDirectory directory = new SupplierDirectory(new InMemorySupplierRepository());
        directory.addIfNew("metro Trading", "1");
        directory.addIfNew("Acme", "2");
        directory.addIfNew("Gold Star", "3");
        assertEquals(List.of("Acme", "Gold Star", "metro Trading"), directory.namesAToZ());
    }

    @Test
    void theDirectoryUsesTheRepositoryItWasGiven() {
        CountingSupplierRepository counting = new CountingSupplierRepository(new InMemorySupplierRepository());
        new SupplierDirectory(counting).addIfNew("Acme", "1"); // one findByName and one add
        assertEquals(2, counting.calls());
    }
}
