package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.javabybuilding.persistence.CustomerJpaRepository;
import dev.javabybuilding.persistence.JpaProductRepository;
import dev.javabybuilding.persistence.JpaSaleRepository;
import dev.javabybuilding.persistence.JpaStockMovementRepository;
import dev.javabybuilding.persistence.ProductJpaRepository;
import dev.javabybuilding.persistence.SaleJpaRepository;
import dev.javabybuilding.persistence.StockMovementJpaRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

/**
 * Slice test: @DataJpaTest starts ONLY the database part of Spring (entities, Spring Data repositories, Flyway, a fresh in-memory
 * H2) and wraps every test method in a transaction that is rolled back at the end, so each test starts on an empty database.
 * It is much faster than starting the whole app. These tests run the SAME contract as the in-memory and JDBC repositories
 * (lesson 30), plus what only JPA needs checking.
 */
@ShopDataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class JpaRepositoriesTest extends SalesRepositoryContract {

    @Autowired ProductJpaRepository productJpa;
    @Autowired SaleJpaRepository saleJpa;
    @Autowired StockMovementJpaRepository movementJpa;
    @Autowired CustomerJpaRepository customerJpa;
    @Autowired TestEntityManager entityManager;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Override
    Repos create() {
        return new Repos(new JpaProductRepository(productJpa), new JpaSaleRepository(saleJpa, customerJpa, productJpa),
                new JpaStockMovementRepository(movementJpa, productJpa));
    }

    private static Sale draftFor(Customer customer, LocalDateTime at, int riceQuantity) {
        Sale sale = new Sale(0, customer, at);
        sale.addLine(new SaleLine("Rice 25kg", Money.of("1250.50"), riceQuantity));
        return sale;
    }

    @Test
    void theSameCustomerIsOneObjectInAllTheirSalesAndTheirPointsAreWorkedOutAgain() {
        LocalDateTime day = LocalDateTime.of(2026, 3, 1, 9, 30, 0);
        repos.sales().add(draftFor(new Customer("Ana Reyes", "09171234567"), day, 2));            // 2501.00 -> 25 points
        repos.sales().add(draftFor(new Customer("Ana Reyes", "09171234567"), day.plusDays(1), 1)); // 1250.50 -> 12 points
        List<Sale> sales = repos.sales().findAll();
        assertSame(sales.get(0).getCustomer(), sales.get(1).getCustomer());
        assertEquals(37, sales.get(0).getCustomer().getPoints());
    }

    @Test
    void aCustomerIsStoredOnceHoweverManyTimesTheyBuy() {
        LocalDateTime day = LocalDateTime.of(2026, 3, 1, 9, 30, 0);
        repos.sales().add(draftFor(new Customer("Ana Reyes", "0917"), day, 1));
        repos.sales().add(draftFor(new Customer("Ana Reyes", "0917"), day, 1));
        assertEquals(1, customerJpa.count());
    }

    @Test
    void aMovementOrSaleLineForAnUnknownProductIsRefused() {
        assertThrows(DatabaseException.class, () -> repos.movements().add(
                new StockMovement("Ghost", MovementType.SOLD, 1, LocalDateTime.of(2026, 3, 1, 9, 0))));
        Sale sale = new Sale(0, Customer.WALK_IN, LocalDateTime.of(2026, 3, 1, 9, 0));
        sale.addLine(new SaleLine("Ghost", Money.of("1.00"), 1));
        assertThrows(DatabaseException.class, () -> repos.sales().add(sale));
    }

    /** The mapping really matches the tables Flyway made: the names of the columns, and the column the entity ignores. */
    @Test
    void theEntityFieldsLandInTheColumnsFlywayCreated() {
        repos.products().add(new Product("Salt 1kg", Money.of("20.00"), 5, 7));
        entityManager.flush();
        Map<String, Object> row = jdbc.queryForMap("SELECT name, price, reorder_level, quantity, active FROM product WHERE name = 'Salt 1kg'");
        assertEquals("Salt 1kg", row.get("NAME"));
        assertEquals(Money.of("20.00"), row.get("PRICE"));
        assertEquals(5, row.get("REORDER_LEVEL"));
        assertEquals(7, row.get("QUANTITY"));
        assertEquals(true, row.get("ACTIVE")); // not mapped by the entity: the database default (migration V3) filled it
    }

    @Test
    void thePaymentIsStoredAsItsNameAndTheDiscountAsItsCode() {
        Sale draft = draftFor(new Customer("Ana Reyes", "0917"), LocalDateTime.of(2026, 3, 1, 9, 30), 1);
        draft.setPayment(PaymentMethod.GCASH);
        draft.setDiscount(new PercentDiscount(10));
        repos.sales().add(draft);
        entityManager.flush();
        Map<String, Object> row = jdbc.queryForMap("SELECT payment, discount, customer_id FROM sale");
        assertEquals("GCASH", row.get("PAYMENT"));
        assertEquals("percent:10", row.get("DISCOUNT"));
        assertNotNull(row.get("CUSTOMER_ID"));
    }

    /** The reason for "join fetch": reading 5 sales with 1 line each costs ONE query, not 1 + 5 + 5. */
    @Test
    void readingAllSalesIsOneQueryNotOnePerSale() {
        LocalDateTime day = LocalDateTime.of(2026, 3, 1, 9, 30);
        for (int i = 0; i < 5; i++) {
            repos.sales().add(draftFor(new Customer("Customer " + i, "09" + i), day.plusMinutes(i), 1));
        }
        entityManager.flush();
        entityManager.clear(); // forget everything in memory, so the next read must go to the database
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        assertEquals(5, repos.sales().findAll().size());
        assertEquals(1, statistics.getPrepareStatementCount());
    }
}
