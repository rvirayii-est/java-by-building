package dev.javabybuilding.persistence;

import dev.javabybuilding.inventory.MovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** One row of stock_movement: a line of the stock log. */
@Entity
@Table(name = "stock_movement")
public class StockMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProductEntity product;

    @JdbcTypeCode(SqlTypes.VARCHAR) // H2 would otherwise send the enum as its own ENUM type; our column is plain VARCHAR
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime movedAt;

    protected StockMovementEntity() {}

    public StockMovementEntity(ProductEntity product, MovementType type, int quantity, LocalDateTime movedAt) {
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.movedAt = movedAt;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public MovementType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getMovedAt() {
        return movedAt;
    }
}
