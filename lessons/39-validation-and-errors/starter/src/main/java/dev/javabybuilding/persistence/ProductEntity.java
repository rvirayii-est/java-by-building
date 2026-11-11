package dev.javabybuilding.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * One row of the product table, as a Java object. This is a JPA ENTITY: the annotations say how the class maps to the table.
 * It is NOT the domain class Product (lesson 10), which has rules and no id; the repository adapter copies between the two.
 *
 * <p>Names: Spring Boot turns a camelCase field into a snake_case column, so reorderLevel is the column reorder_level.
 * The column active (migration V3) is not mapped: the database fills it with its default and the app never reads it.
 */
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id // the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // the database makes the number (GENERATED ... AS IDENTITY in V1)
    private Integer id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int reorderLevel;

    @Column(nullable = false)
    private int quantity;

    protected ProductEntity() {} // JPA creates objects with this constructor, then fills the fields; it is not for our code

    public ProductEntity(String name, BigDecimal price, int reorderLevel, int quantity) {
        this.name = name;
        this.price = price;
        this.reorderLevel = reorderLevel;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Copies price, reorder level and quantity from the domain product (the name never changes). */
    public void changeTo(BigDecimal price, int reorderLevel, int quantity) {
        this.price = price;
        this.reorderLevel = reorderLevel;
        this.quantity = quantity;
    }
}
