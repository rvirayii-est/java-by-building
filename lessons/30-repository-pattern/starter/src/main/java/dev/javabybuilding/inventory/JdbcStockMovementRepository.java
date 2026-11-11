package dev.javabybuilding.inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** The stock log in the stock_movement table. */
public class JdbcStockMovementRepository implements StockMovementRepository {

    private final Database database;

    public JdbcStockMovementRepository(Database database) {
        this.database = database;
    }

    @Override
    public void add(StockMovement movement) {
        // TODO 2a: INSERT INTO stock_movement (product_id, type, quantity, moved_at) VALUES ((SELECT id FROM product WHERE name = ?), ?, ?, ?)
        // setString(1, productName), setString(2, type().name()), setInt(3, quantity), setObject(4, at); executeUpdate.
        // Wrap SQLException in new DatabaseException("Cannot log the stock movement of ...", e).
        throw new UnsupportedOperationException("TODO 2a");
    }

    @Override
    public List<StockMovement> findAll() {
        // TODO 2b: SELECT p.name, m.type, m.quantity, m.moved_at FROM stock_movement m JOIN product p ON p.id = m.product_id ORDER BY m.id
        // For each row: new StockMovement(name, MovementType.valueOf(type), quantity, rows.getObject("moved_at", LocalDateTime.class)).
        throw new UnsupportedOperationException("TODO 2b");
    }
}
