package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/** The stock log kept in a list in memory (for fast tests). */
public class InMemoryStockMovementRepository implements StockMovementRepository {

    private final List<StockMovement> movements = new ArrayList<>();

    @Override
    public void add(StockMovement movement) {
        movements.add(movement);
    }

    @Override
    public List<StockMovement> findAll() {
        return List.copyOf(movements);
    }
}
