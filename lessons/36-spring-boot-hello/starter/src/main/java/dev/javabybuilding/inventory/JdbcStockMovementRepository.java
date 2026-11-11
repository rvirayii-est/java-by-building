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
        String sql = "INSERT INTO stock_movement (product_id, type, quantity, moved_at) "
                + "VALUES ((SELECT id FROM product WHERE name = ?), ?, ?, ?)";
        try (PreparedStatement statement = database.connection().prepareStatement(sql)) {
            statement.setString(1, movement.productName());
            statement.setString(2, movement.type().name());
            statement.setInt(3, movement.quantity());
            statement.setObject(4, movement.at());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Cannot log the stock movement of " + movement.productName(), e);
        }
    }

    @Override
    public List<StockMovement> findAll() {
        String sql = "SELECT p.name, m.type, m.quantity, m.moved_at FROM stock_movement m "
                + "JOIN product p ON p.id = m.product_id ORDER BY m.id";
        try (PreparedStatement statement = database.connection().prepareStatement(sql); ResultSet rows = statement.executeQuery()) {
            List<StockMovement> movements = new ArrayList<>();
            while (rows.next()) {
                movements.add(new StockMovement(rows.getString("name"), MovementType.valueOf(rows.getString("type")),
                        rows.getInt("quantity"), rows.getObject("moved_at", LocalDateTime.class)));
            }
            return movements;
        } catch (SQLException e) {
            throw new DatabaseException("Cannot read the stock log", e);
        }
    }
}
