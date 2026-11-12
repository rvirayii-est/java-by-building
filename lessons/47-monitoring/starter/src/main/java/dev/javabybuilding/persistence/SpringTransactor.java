package dev.javabybuilding.persistence;

import dev.javabybuilding.inventory.Transactor;
import java.util.function.Supplier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Lesson 31's Transactor, done by Spring. {@code Database.inTransaction} switched auto-commit off, committed, rolled back and
 * joined a running transaction by hand; a {@link TransactionTemplate} does all four. Work that returns normally is committed;
 * work that throws a RuntimeException is rolled back and the same exception arrives at the caller. ShopService does not change:
 * it only knows the Transactor interface.
 */
public class SpringTransactor implements Transactor {

    private final TransactionTemplate template;

    public SpringTransactor(PlatformTransactionManager transactionManager) {
        this.template = new TransactionTemplate(transactionManager); // the default: join a running transaction, else start one
    }

    @Override
    public <T> T inTransaction(Supplier<T> work) {
        return template.execute(status -> work.get());
    }
}
