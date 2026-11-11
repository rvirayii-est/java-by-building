package dev.javabybuilding.inventory;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The shop's rules (what a sale does to stock, to the stock log and to the customer), written against three
 * repository INTERFACES. It has no idea whether products live in a database, a file or memory. Compare with
 * {@link Inventory}, which keeps everything in its own lists: same rules, same messages, different storage.
 *
 * <p>Every method that changes something does several steps (update the product, store the sale, log the movement).
 * They run inside {@code transactor.inTransaction(...)} so they succeed or fail TOGETHER (lesson 31). With a
 * {@link Database} as the Transactor that is a real database transaction.
 */
public class ShopService implements Shop {

    private static final Logger LOG = Logger.getLogger(ShopService.class.getName());

    private final ProductRepository products;
    private final SaleRepository sales;
    private final StockMovementRepository movements;
    private final Clock clock;
    private final Transactor transactor;

    /** A service whose multi-step jobs are NOT protected by a transaction (fine for in-memory repositories). */
    public ShopService(ProductRepository products, SaleRepository sales, StockMovementRepository movements, Clock clock) {
        this(products, sales, movements, clock, Transactor.NONE);
    }

    /** A service whose multi-step jobs run in the transaction the Transactor provides (pass the Database). */
    public ShopService(ProductRepository products, SaleRepository sales, StockMovementRepository movements, Clock clock,
                       Transactor transactor) {
        this.products = products;
        this.sales = sales;
        this.movements = movements;
        this.clock = clock;
        this.transactor = transactor;
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public String find(String name) {
        return products.findByName(name).map(Product::getName).orElse(null);
    }

    @Override
    public Product getProduct(String name) {
        return products.findByName(name).orElseThrow(() -> new UnknownProductException(name));
    }

    @Override
    public void addProduct(String name, BigDecimal price, int reorderLevel) {
        String clean = TextUtil.clean(name);
        transactor.runInTransaction(() -> {
            products.findByName(clean).ifPresent(existing -> {
                throw new InvalidInputException("That product already exists: " + existing.getName());
            });
            products.add(new Product(clean, price, reorderLevel)); // the Product constructor checks the rest
        });
    }

    @Override
    public int quantityOf(String name) {
        return getProduct(name).getQuantity();
    }

    @Override
    public void receive(String name, int amount) {
        Product received = transactor.inTransaction(() -> {
            Product product = getProduct(name);
            product.receive(amount); // throws before anything is stored
            products.update(product);                                                              // step 1
            movements.add(new StockMovement(product.getName(), MovementType.RECEIVED, amount, now())); // step 2
            return product;
        });
        LOG.log(Level.INFO, "Received {0} x {1} (now {2} in stock)", new Object[] {amount, received.getName(), received.getQuantity()});
    }

    @Override
    public Sale sell(String name, int amount, Customer customer, Discount discount, PaymentMethod payment) {
        // All three steps are ONE transaction: if any step throws, the others are undone, so the shelf, the receipt
        // and the stock log can never disagree.
        Sale sale = transactor.inTransaction(() -> {
            Product product = getProduct(name);
            product.sell(amount); // fails first when there is not enough stock, before anything is stored
            products.update(product);                                        // step 1: take the units off the shelf
            Sale draft = new Sale(0, customer, now());
            draft.addLine(new SaleLine(product.getName(), product.getPrice(), amount));
            draft.setDiscount(discount);
            draft.setPayment(payment);
            Sale stored = sales.add(draft);                                  // step 2: store the receipt
            movements.add(new StockMovement(product.getName(), MovementType.SOLD, amount, now())); // step 3: log it
            return stored;
        });
        customer.addPurchase(sale.total()); // after the commit: points are only for sales that really happened
        LOG.info(() -> "Sale #" + sale.getNumber() + ": " + amount + " x " + sale.getLines().get(0).productName()
                + " for " + PriceCalculator.format(sale.total()) + " (" + payment.label() + ")");
        return sale;
    }

    @Override
    public List<Product> products() {
        return products.findAll();
    }

    @Override
    public List<Sale> sales() {
        return sales.findAll();
    }

    @Override
    public List<StockMovement> movements() {
        return movements.findAll();
    }

    @Override
    public String report() {
        return StockReport.build(products());
    }

    @Override
    public BigDecimal totalValue() {
        return StockReport.totalValue(products());
    }

    @Override
    public BigDecimal salesTotal() {
        return SalesReport.totalRevenue(sales());
    }

    @Override
    public List<ReorderSuggestion> reorderSuggestions() {
        return ReorderAdvisor.suggestions(products());
    }

    @Override
    public List<String> audit() {
        List<StockMovement> log = movements();
        List<String> problems = new ArrayList<>();
        for (Product product : products()) {
            int logged = log.stream().filter(m -> m.productName().equals(product.getName())).mapToInt(StockMovement::signedQuantity).sum();
            if (logged != product.getQuantity()) {
                problems.add(product.getName() + ": the shelf has " + product.getQuantity() + " but the stock log adds up to " + logged);
            }
        }
        return problems;
    }

    /**
     * Copies everything an Inventory holds (its products with their quantities, its sales and its stock log) into the
     * repositories. Used the first time the app runs on a database: from the sample shop, or from the CSV files of
     * lesson 19. Nothing is made up: the stock log you had is the stock log you get.
     */
    public void importFrom(Inventory inventory) {
        transactor.runInTransaction(() -> { // all or nothing: a half-imported shop would be worse than none
            for (Product product : inventory.products()) {
                products.add(product);
            }
            for (Sale sale : inventory.sales()) {
                sales.add(sale);
            }
            for (StockMovement movement : inventory.movements()) {
                movements.add(movement);
            }
        });
    }
}
