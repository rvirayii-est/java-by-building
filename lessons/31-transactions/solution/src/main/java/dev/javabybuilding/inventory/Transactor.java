package dev.javabybuilding.inventory;

import java.util.function.Supplier;

/**
 * Runs a piece of work as ONE unit: either every step in it happens, or none does (a transaction).
 * ShopService asks a Transactor to run its multi-step jobs; it does not know whether the Transactor is a database or
 * does nothing at all. {@link Database} is the real one. {@link #NONE} just runs the work (for in-memory repositories,
 * which have nothing to roll back).
 */
public interface Transactor {

    /**
     * Runs the work. If it returns, everything it did is saved ("committed"). If it throws, everything it did to the
     * database is undone ("rolled back") and the same exception is thrown to the caller. If a transaction is already
     * running, the work simply joins it: the OUTERMOST call decides to commit or roll back.
     */
    <T> T inTransaction(Supplier<T> work);

    /** The same for work that gives no result. */
    default void runInTransaction(Runnable work) {
        inTransaction(() -> {
            work.run();
            return null;
        });
    }

    /** A Transactor that does not protect anything: it just runs the work. */
    Transactor NONE = new Transactor() {
        @Override
        public <T> T inTransaction(Supplier<T> work) {
            return work.get();
        }
    };
}
