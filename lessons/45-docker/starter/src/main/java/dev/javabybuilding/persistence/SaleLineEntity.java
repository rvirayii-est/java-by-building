package dev.javabybuilding.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * One row of sale_line. {@code @ManyToOne} is the foreign key of lesson 28 as an object: a line has ONE sale and ONE product,
 * and the columns sale_id and product_id are filled in for us.
 */
@Entity
@Table(name = "sale_line")
public class SaleLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // LAZY: do not load the sale until somebody asks for it
    private SaleEntity sale;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProductEntity product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    protected SaleLineEntity() {}

    SaleLineEntity(SaleEntity sale, ProductEntity product, BigDecimal unitPrice, int quantity) {
        this.sale = sale;
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
