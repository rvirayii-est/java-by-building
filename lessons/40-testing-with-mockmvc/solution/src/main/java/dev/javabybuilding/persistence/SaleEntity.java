package dev.javabybuilding.persistence;

import dev.javabybuilding.inventory.PaymentMethod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** One row of the sale table, with its lines. */
@Entity
@Table(name = "sale")
public class SaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // optional: a walk-in has no customer row (customer_id is NULL)
    private CustomerEntity customer;

    @Column(nullable = false)
    private LocalDateTime soldAt;

    @Column(nullable = false, length = 20)
    private String discount; // the code of lesson 19: none, percent:10, fixed:50.00, senior

    @JdbcTypeCode(SqlTypes.VARCHAR) // H2 would otherwise send the enum as its own ENUM type; our column is plain VARCHAR
    @Enumerated(EnumType.STRING) // stored as the name: CASH, GCASH, CARD (never as a number)
    @Column(nullable = false, length = 10)
    private PaymentMethod payment;

    // mappedBy: the foreign key lives in sale_line, so SaleLineEntity.sale owns the link. cascade ALL: saving the sale saves its lines.
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleLineEntity> lines = new ArrayList<>();

    protected SaleEntity() {}

    public SaleEntity(CustomerEntity customer, LocalDateTime soldAt, String discount, PaymentMethod payment) {
        this.customer = customer;
        this.soldAt = soldAt;
        this.discount = discount;
        this.payment = payment;
    }

    /** Adds a line to this sale (and tells the line which sale it belongs to). */
    public void addLine(ProductEntity product, BigDecimal unitPrice, int quantity) {
        lines.add(new SaleLineEntity(this, product, unitPrice, quantity));
    }

    public Integer getId() {
        return id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public String getDiscount() {
        return discount;
    }

    public PaymentMethod getPayment() {
        return payment;
    }

    public List<SaleLineEntity> getLines() {
        return lines;
    }
}
