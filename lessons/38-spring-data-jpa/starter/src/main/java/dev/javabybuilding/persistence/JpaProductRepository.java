package dev.javabybuilding.persistence;

import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.Product;
import dev.javabybuilding.inventory.ProductRepository;
import dev.javabybuilding.inventory.TextUtil;
import dev.javabybuilding.inventory.UnknownProductException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

/**
 * The ProductRepository of lessons 30-32, now on JPA. ShopService does not know: it still sees the same interface, and the
 * same contract tests (ProductRepositoryContract) pass. This class is an ADAPTER: it translates between the domain
 * {@link Product} (rules, no id) and the {@link ProductEntity} (a table row with an id) that Spring Data stores.
 *
 * <p>{@code @Repository} marks it as a bean, and also makes Spring turn database-specific exceptions into its own family
 * (DataAccessException), whatever the database is.
 */
@Repository
public class JpaProductRepository implements ProductRepository {

    private final ProductJpaRepository jpa;

    public JpaProductRepository(ProductJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void add(Product product) {
        if (jpa.findByNameIgnoreCase(product.getName()).isPresent()) {
            throw new InvalidInputException("That product already exists: " + product.getName());
        }
        try {
            jpa.saveAndFlush(new ProductEntity(product.getName(), product.getPrice(), product.getReorderLevel(), product.getQuantity()));
        } catch (DataIntegrityViolationException e) { // two people added the same name at the same moment
            throw new InvalidInputException("That product already exists: " + product.getName());
        }
    }

    @Override
    public Optional<Product> findByName(String name) {
        return jpa.findByNameIgnoreCase(TextUtil.clean(name)).map(JpaProductRepository::toProduct);
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAllByOrderByIdAsc().stream().map(JpaProductRepository::toProduct).toList();
    }

    @Override
    public void update(Product product) {
        // TODO 2: find the entity with jpa.findByNameIgnoreCase(product.getName()) (throw new UnknownProductException(product.getName())
        // when it is empty), copy the new values into it with entity.changeTo(price, reorderLevel, quantity), and store it with jpa.save(entity).
        throw new UnsupportedOperationException("TODO 2");
    }

    @Override
    public void delete(String name) {
        // TODO 2 (continued): find the entity by TextUtil.clean(name) the same way (UnknownProductException when missing), then
        // jpa.delete(entity) and jpa.flush() so a foreign-key refusal is reported here.
        throw new UnsupportedOperationException("TODO 2");
    }

    /** Entity to domain object. The Product constructor checks the rules again, so a bad row in the table cannot sneak in. */
    static Product toProduct(ProductEntity entity) {
        return new Product(entity.getName(), entity.getPrice(), entity.getReorderLevel(), entity.getQuantity());
    }
}
