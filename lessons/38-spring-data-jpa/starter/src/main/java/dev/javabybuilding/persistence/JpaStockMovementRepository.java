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
        // TODO 3: find the product with products.findByNameIgnoreCase(TextUtil.clean(movement.productName())); when there is none throw
        // new DatabaseException("Cannot log the movement: there is no product " + movement.productName()). Then
        // movements.saveAndFlush(new StockMovementEntity(product, movement.type(), movement.quantity(), movement.at())).
        throw new UnsupportedOperationException("TODO 3");
    }

    @Override
    public List<StockMovement> findAll() {
        // TODO 3 (continued): movements.findAllWithProduct() gives the entities in order; map each to new StockMovement(
        // row.getProduct().getName(), row.getType(), row.getQuantity(), row.getMovedAt()).
        return List.of();
    }
}
