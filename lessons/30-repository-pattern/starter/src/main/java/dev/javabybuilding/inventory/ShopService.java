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
 * Lesson 31 makes those steps all-or-nothing.
 */
public class ShopService implements Shop {

    private static final Logger LOG = Logger.getLogger(ShopService.class.getName());

    private final ProductRepository products;
    private final SaleRepository sales;
    private final StockMovementRepository movements;
    private final Clock clock;

    public ShopService(ProductRepository products, SaleRepository sales, StockMovementRepository movements, Clock clock) {
        this.products = products;
        this.sales = sales;
        this.movements = movements;
        this.clock = clock;
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
        products.findByName(clean).ifPresent(existing -> {
            throw new InvalidInputException("That product already exists: " + existing.getName());
        });
        products.add(new Product(clean, price, reorderLevel)); // the Product constructor checks the rest
    }

    @Override
    public int quantityOf(String name) {
        return getProduct(name).getQuantity();
    }

    @Override
    public void receive(String name, int amount) {
        // TODO 3: (1) Product product = getProduct(name); (2) product.receive(amount); (3) products.update(product);
        // (4) movements.add(new StockMovement(product.getName(), MovementType.RECEIVED, amount, now())); (5) log it like Inventory does.
        throw new UnsupportedOperationException("TODO 3");
    }

    @Override
    public Sale sell(String name, int amount, Customer customer, Discount discount, PaymentMethod payment) {
        // TODO 4: the same steps as Inventory.sell, but through the repositories:
        // (1) getProduct(name) and product.sell(amount) (throws when there is not enough stock, before anything is stored);
        // (2) products.update(product); (3) build a draft Sale(0, customer, now()) with one SaleLine(name, price, amount),
        // setDiscount, setPayment; Sale sale = sales.add(draft); (4) movements.add(SOLD movement);
        // (5) customer.addPurchase(sale.total()); (6) log "Sale #n: ..." at INFO; return sale.
        throw new UnsupportedOperationException("TODO 4");
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
        for (Product product : inventory.products()) {
            products.add(product);
        }
        for (Sale sale : inventory.sales()) {
            sales.add(sale);
        }
        for (StockMovement movement : inventory.movements()) {
            movements.add(movement);
        }
    }
}
