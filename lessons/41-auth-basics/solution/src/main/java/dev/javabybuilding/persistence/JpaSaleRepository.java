package dev.javabybuilding.persistence;

import dev.javabybuilding.inventory.Customer;
import dev.javabybuilding.inventory.DatabaseException;
import dev.javabybuilding.inventory.DiscountCodes;
import dev.javabybuilding.inventory.Sale;
import dev.javabybuilding.inventory.SaleLine;
import dev.javabybuilding.inventory.SaleRepository;
import dev.javabybuilding.inventory.TextUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 * The SaleRepository on JPA. Same job as JdbcSaleRepository of lesson 30; compare the two: the SQL is gone, but so is the
 * visible cost of each query, which is the price of an ORM (see the README).
 */
@Repository
public class JpaSaleRepository implements SaleRepository {

    private final SaleJpaRepository sales;
    private final CustomerJpaRepository customers;
    private final ProductJpaRepository products;

    public JpaSaleRepository(SaleJpaRepository sales, CustomerJpaRepository customers, ProductJpaRepository products) {
        this.sales = sales;
        this.customers = customers;
        this.products = products;
    }

    @Override
    public Sale add(Sale draft) {
        CustomerEntity customer = draft.getCustomer().isWalkIn() ? null : findOrCreate(draft.getCustomer());
        SaleEntity entity = new SaleEntity(customer, draft.getDateTime(), DiscountCodes.encode(draft.getDiscount()), draft.getPayment());
        for (SaleLine line : draft.getLines()) {
            ProductEntity product = products.findByNameIgnoreCase(TextUtil.clean(line.productName()))
                    .orElseThrow(() -> new DatabaseException("Cannot save the sale: there is no product " + line.productName()));
            entity.addLine(product, line.unitPrice(), line.quantity());
        }
        SaleEntity saved = sales.saveAndFlush(entity); // cascade: the lines are saved with the sale
        return draft.withNumber(saved.getId());
    }

    private CustomerEntity findOrCreate(Customer customer) {
        String phone = customer.getPhone() == null ? "" : customer.getPhone();
        return customers.findByNameAndPhone(customer.getName(), phone).orElseGet(() -> customers.save(new CustomerEntity(customer.getName(), phone)));
    }

    @Override
    public List<Sale> findAll() {
        Map<String, Customer> people = new HashMap<>(); // one Customer object per person
        List<Sale> result = new ArrayList<>();
        for (SaleEntity entity : sales.findAllWithDetails()) {
            Customer customer = Customer.WALK_IN;
            if (entity.getCustomer() != null) {
                CustomerEntity row = entity.getCustomer();
                customer = people.computeIfAbsent(row.getName() + "|" + row.getPhone(), key -> new Customer(row.getName(), row.getPhone()));
            }
            Sale sale = new Sale(entity.getId(), customer, entity.getSoldAt());
            sale.setDiscount(DiscountCodes.decode(entity.getDiscount()));
            sale.setPayment(entity.getPayment());
            entity.getLines().stream().sorted(Comparator.comparing(SaleLineEntity::getId)).forEach(line ->
                    sale.addLine(new SaleLine(line.getProduct().getName(), line.getUnitPrice(), line.getQuantity())));
            result.add(sale);
        }
        for (Sale sale : result) {
            sale.getCustomer().addPurchase(sale.total()); // points are worked out again from the sales, as in lesson 30
        }
        return result;
    }
}
