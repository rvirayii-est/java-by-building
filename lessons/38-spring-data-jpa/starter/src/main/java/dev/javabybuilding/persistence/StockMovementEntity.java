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
// TODO 1: map this class like ProductEntity and SaleEntity. It needs, in this order:
//   on the class:        @Entity and @Table(name = "stock_movement")
//   on id:               @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
//   on product:          @ManyToOne(optional = false, fetch = FetchType.LAZY)   (the foreign key product_id)
//   on type:             @JdbcTypeCode(SqlTypes.VARCHAR), @Enumerated(EnumType.STRING) and @Column(nullable = false, length = 10)
//   on quantity, movedAt: @Column(nullable = false)
// The start-up check (ddl-auto=validate) and the tests tell you what is still missing.
public class StockMovementEntity {

    private Integer id;

    private ProductEntity product;

    private MovementType type;

    private int quantity;

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
