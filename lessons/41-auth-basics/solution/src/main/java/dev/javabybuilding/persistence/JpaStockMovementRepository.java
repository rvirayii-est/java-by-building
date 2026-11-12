package dev.javabybuilding.persistence;

import dev.javabybuilding.inventory.DatabaseException;
import dev.javabybuilding.inventory.StockMovement;
import dev.javabybuilding.inventory.StockMovementRepository;
import dev.javabybuilding.inventory.TextUtil;
import java.util.List;
import org.springframework.stereotype.Repository;

/** The StockMovementRepository on JPA: the stock log. */
@Repository
public class JpaStockMovementRepository implements StockMovementRepository {

    private final StockMovementJpaRepository movements;
    private final ProductJpaRepository products;

    public JpaStockMovementRepository(StockMovementJpaRepository movements, ProductJpaRepository products) {
        this.movements = movements;
        this.products = products;
    }

    @Override
    public void add(StockMovement movement) {
        ProductEntity product = products.findByNameIgnoreCase(TextUtil.clean(movement.productName()))
                .orElseThrow(() -> new DatabaseException("Cannot log the movement: there is no product " + movement.productName()));
        movements.saveAndFlush(new StockMovementEntity(product, movement.type(), movement.quantity(), movement.at()));
    }

    @Override
    public List<StockMovement> findAll() {
        return movements.findAllWithProduct().stream()
                .map(row -> new StockMovement(row.getProduct().getName(), row.getType(), row.getQuantity(), row.getMovedAt()))
                .toList();
    }
}
