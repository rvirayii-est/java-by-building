package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

/** The rules every SaleRepository and StockMovementRepository must follow. Products Rice and Sugar exist before each test. */
@Transactional // for the JPA tests (@DataJpaTest): every test runs in a transaction that is rolled back; the in-memory tests ignore it
abstract class SalesRepositoryContract {

    /** The three repositories of one storage, with a fresh empty store. */
    record Repos(ProductRepository products, SaleRepository sales, StockMovementRepository movements) {}

    abstract Repos create();

    Repos repos;
    private static final LocalDateTime MORNING = LocalDateTime.of(2026, 3, 1, 9, 30, 0);

    @BeforeEach
    void createRepositories() {
        repos = create();
        repos.products().add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        repos.products().add(new Product("Sugar 1kg", Money.of("75.00"), 10, 4));
    }

    private static Sale draft(Customer customer, LocalDateTime at, SaleLine... lines) {
        Sale sale = new Sale(0, customer, at);
        for (SaleLine line : lines) {
            sale.addLine(line);
        }
        return sale;
    }

    private static SaleLine rice(int quantity) {
        return new SaleLine("Rice 25kg", Money.of("1250.50"), quantity);
    }

    @Test
    void addNumbersTheSalesOneAfterAnother() {
        // A database keeps counting after a rolled-back test, so the first number is not always 1: only "next = previous + 1" is promised.
        int first = repos.sales().add(draft(Customer.WALK_IN, MORNING, rice(1))).getNumber();
        assertEquals(first + 1, repos.sales().add(draft(Customer.WALK_IN, MORNING, rice(1))).getNumber());
    }

    @Test
    void aStoredSaleKeepsItsLinesTimeDiscountAndPayment() {
        Sale draft = draft(Customer.WALK_IN, MORNING, rice(2), new SaleLine("Sugar 1kg", Money.of("75.00"), 3));
        draft.setDiscount(new PercentDiscount(10));
        draft.setPayment(PaymentMethod.GCASH);
        Sale stored = repos.sales().add(draft);

        Sale read = repos.sales().findAll().get(0);
        assertEquals(stored.getNumber(), read.getNumber());
        assertEquals(MORNING, read.getDateTime());
        assertEquals(PaymentMethod.GCASH, read.getPayment());
        assertEquals("percent:10", DiscountCodes.encode(read.getDiscount()));
        assertEquals(List.of(rice(2), new SaleLine("Sugar 1kg", Money.of("75.00"), 3)), read.getLines());
        assertEquals(draft.total(), read.total());
    }

    @Test
    void findAllReturnsTheOldestSaleFirst() {
        int first = repos.sales().add(draft(Customer.WALK_IN, MORNING, rice(1))).getNumber();
        int second = repos.sales().add(draft(Customer.WALK_IN, MORNING.plusHours(1), rice(2))).getNumber();
        assertEquals(List.of(first, second), repos.sales().findAll().stream().map(Sale::getNumber).toList());
    }

    @Test
    void aWalkInSaleComesBackAsTheWalkInCustomer() {
        repos.sales().add(draft(Customer.WALK_IN, MORNING, rice(1)));
        assertTrue(repos.sales().findAll().get(0).getCustomer().isWalkIn());
    }

    @Test
    void aCustomersNameAndPhoneComeBack() {
        repos.sales().add(draft(new Customer("Ana Reyes", "09171234567"), MORNING, rice(2)));
        Customer customer = repos.sales().findAll().get(0).getCustomer();
        assertEquals("Ana Reyes", customer.getName());
        assertEquals("09171234567", customer.getPhone());
    }

    @Test
    void twoPeopleWithTheSameNameButDifferentPhonesStayTwoCustomers() {
        repos.sales().add(draft(new Customer("Ana Reyes", "0917"), MORNING, rice(1)));
        repos.sales().add(draft(new Customer("Ana Reyes", "0999"), MORNING, rice(1)));
        List<Sale> sales = repos.sales().findAll();
        assertEquals("0917", sales.get(0).getCustomer().getPhone());
        assertEquals("0999", sales.get(1).getCustomer().getPhone());
    }

    @Test
    void noSalesAtFirst() {
        assertTrue(repos.sales().findAll().isEmpty());
        assertTrue(repos.movements().findAll().isEmpty());
    }

    @Test
    void theStockLogKeepsOrderTypeQuantityAndTime() {
        repos.movements().add(new StockMovement("Rice 25kg", MovementType.RECEIVED, 20, MORNING));
        repos.movements().add(new StockMovement("Sugar 1kg", MovementType.SOLD, 3, MORNING.plusMinutes(5)));
        assertEquals(List.of(new StockMovement("Rice 25kg", MovementType.RECEIVED, 20, MORNING),
                new StockMovement("Sugar 1kg", MovementType.SOLD, 3, MORNING.plusMinutes(5))), repos.movements().findAll());
    }
}
