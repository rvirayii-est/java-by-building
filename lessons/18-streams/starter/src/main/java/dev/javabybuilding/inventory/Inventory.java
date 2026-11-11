package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The shop's stock: a list of Products, a list of Sales (receipts) and the stock log. Problems are reported by
 * throwing exceptions (see ShopException), not by returning -1, false or null as lessons 07-15 did.
 * Money is whole centavos (int), as always.
 * Lesson 17: the products now live in a Map (name to Product), so finding one no longer walks the whole list.
 */
public class Inventory {

    // A LinkedHashMap finds a key at once AND remembers the order products were added. The key is the cleaned, lower-case name.
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final ArrayList<Sale> sales = new ArrayList<>();
    private final ArrayList<StockMovement> movements = new ArrayList<>(); // the stock log: every unit in or out

    /** The four products from the stock report, with the same numbers as lessons 03-08. */
    public static Inventory sample() {
        Inventory inventory = new Inventory();
        inventory.addProduct("Rice 25kg", 125050, 10);
        inventory.addProduct("Cooking Oil 1L", 9800, 10);
        inventory.addProduct("Sugar 1kg", 7500, 10);
        inventory.addProduct("Sardines 155g", 2400, 20);
        inventory.receive("Rice 25kg", 12);
        inventory.receive("Cooking Oil 1L", 4);
        inventory.receive("Sardines 155g", 60);
        return inventory;
    }

    /** The Product object whose name matches (ignoring capitals and spaces), or null when there is none. */
    private Product findProduct(String name) {
        return products.get(key(name));
    }

    /** "  Rice   25KG " and "rice 25kg" are the same product, so both turn into the same key: "rice 25kg". */
    private static String key(String name) {
        return TextUtil.clean(name).toLowerCase();
    }

    /** The name as stored (for example "Sugar 1kg") when name matches a product ignoring capitals and spaces, else null. */
    public String find(String name) {
        Product product = findProduct(name);
        if (product == null) {
            return null;
        }
        return product.getName();
    }

    /**
     * The product with this name (ignoring capitals and spaces).
     *
     * @throws UnknownProductException when there is none
     */
    public Product getProduct(String name) {
        Product product = findProduct(name);
        if (product == null) {
            throw new UnknownProductException(name);
        }
        return product;
    }

    /**
     * Adds a new product with 0 in stock. Nothing changes if it throws.
     *
     * @throws InvalidInputException when the name is blank or already exists, the price is not allowed, or the
     *         reorder level is below 0
     */
    public void addProduct(String name, int priceCentavos, int reorderLevel) {
        String clean = TextUtil.clean(name);
        Product existing = findProduct(clean);
        if (existing != null) {
            throw new InvalidInputException("That product already exists: " + existing.getName());
        }
        products.put(key(clean), new Product(clean, priceCentavos, reorderLevel)); // the Product constructor checks the rest
    }

    /**
     * Units in stock.
     *
     * @throws UnknownProductException when there is no such product (lesson 15 returned -1)
     */
    public int quantityOf(String name) {
        return getProduct(name).getQuantity();
    }

    /**
     * Unit price in centavos.
     *
     * @throws UnknownProductException when there is no such product (lesson 15 returned -1)
     */
    public int priceOf(String name) {
        return getProduct(name).getPriceCentavos();
    }

    /**
     * Adds amount units and logs a RECEIVED movement.
     *
     * @throws UnknownProductException when there is no such product
     * @throws InvalidInputException when amount is 0 or less
     */
    public void receive(String name, int amount) {
        Product product = getProduct(name);
        product.receive(amount); // throws before anything is logged
        movements.add(new StockMovement(product.getName(), MovementType.RECEIVED, amount));
    }

    /**
     * Changes a product price.
     *
     * @throws UnknownProductException when there is no such product
     * @throws InvalidInputException when the price is not allowed
     */
    public void changePrice(String name, int newPriceCentavos) {
        getProduct(name).setPrice(newPriceCentavos);
    }

    /**
     * A snapshot of the product list, in the order they were added. It is a copy: adding products to the inventory
     * later does not change a list you already have, and you cannot add to or remove from it.
     * (The Product objects inside are the real ones, though.)
     */
    public List<Product> products() {
        return List.copyOf(products.values());
    }

    /** Sells to a customer at full price (no discount), paid in cash. See the five-argument version. */
    public Sale sell(String name, int amount, Customer customer) {
        return sell(name, amount, customer, new NoDiscount());
    }

    /** Sells with a discount, paid in cash. See the five-argument version. */
    public Sale sell(String name, int amount, Customer customer, Discount discount) {
        return sell(name, amount, customer, discount, PaymentMethod.CASH);
    }

    /**
     * Sells amount units to a customer with a discount and a payment method. Records a Sale and a SOLD stock movement,
     * gives the customer points for the discounted total, and returns the Sale.
     * If it throws, NOTHING changed: no stock taken, no sale, no points, no log entry.
     *
     * @throws UnknownProductException when there is no such product
     * @throws InvalidInputException when amount is 0 or less
     * @throws InsufficientStockException when there is not enough stock
     */
    public Sale sell(String name, int amount, Customer customer, Discount discount, PaymentMethod payment) {
        Product product = getProduct(name);
        product.sell(amount); // the only step that can throw for stock reasons, so it comes first
        Sale sale = new Sale(sales.size() + 1, customer);
        sale.addLine(new SaleLine(product.getName(), product.getPriceCentavos(), amount));
        sale.setDiscount(discount);
        sale.setPayment(payment);
        customer.addPurchase(sale.total());
        sales.add(sale);
        movements.add(new StockMovement(product.getName(), MovementType.SOLD, amount));
        return sale;
    }

    /**
     * Sells amount units to an anonymous walk-in customer. Returns the sale total in centavos (unit price x amount).
     * Throws the same exceptions as the five-argument sell (lesson 15 returned -1 here).
     */
    public int sell(String name, int amount) {
        return sell(name, amount, Customer.WALK_IN).total();
    }

    public int size() {
        return products.size();
    }

    /** Everything sold so far, in centavos: the total of every Sale. shortcut: an int overflows near PHP 21 million; week 5 uses BigDecimal. */
    public int salesTotal() {
        return SalesReport.totalRevenue(sales);
    }

    /** A snapshot of the stock log: every accepted receive and sale, oldest first. */
    public List<StockMovement> movements() {
        return List.copyOf(movements);
    }

    /**
     * Units received minus units sold for one product, according to the log. Because a product starts with 0 in
     * stock, this always equals quantityOf(name): the log and the shelf agree.
     *
     * @throws UnknownProductException when there is no such product
     */
    public int netMovement(String name) {
        String stored = getProduct(name).getName();
        int net = 0;
        for (StockMovement movement : movements) {
            if (movement.productName().equals(stored)) {
                net += movement.signedQuantity();
            }
        }
        return net;
    }

    /** A sorted copy of the product list. The Comparator decides the order: see ProductOrder for ready-made ones. */
    public List<Product> sortedProducts(Comparator<Product> order) {
        List<Product> sorted = new ArrayList<>(products.values());
        sorted.sort(order);
        return sorted;
    }

    /** How many units of each product have been sold, counted from the stock log. */
    public Counter<String> unitsSoldByProduct() {
        Counter<String> sold = new Counter<>();
        // TODO 5c (refactor): turn this loop into movements.stream().filter(...).forEach(...)
        for (StockMovement movement : movements) {
            if (movement.type() == MovementType.SOLD) {
                sold.add(movement.productName(), movement.quantity());
            }
        }
        return sold;
    }

    /** The products that are LOW or OUT, in the order they were added. */
    public List<Product> needsRestock() {
        return SalesReport.needsRestock(products());
    }

    /** The names of the n products that sold the most units, best first (fewer when fewer have sold). */
    public List<String> bestSellers(int n) {
        return unitsSoldByProduct().top(n);
    }

    /**
     * The names of everyone who bought something, each once, in A-Z order. The walk-in customer is not a person, so it is left out.
     * A Set never holds the same value twice; a TreeSet also keeps its values sorted.
     */
    public Set<String> buyerNames() {
        // TODO 5d (refactor): turn this loop into sales.stream().map(...).filter(...).map(...).collect(Collectors.toCollection(TreeSet::new))
        TreeSet<String> names = new TreeSet<>();
        for (Sale sale : sales) {
            if (!sale.getCustomer().isWalkIn()) {
                names.add(sale.getCustomer().getName());
            }
        }
        return names;
    }

    /** A snapshot of every sale made so far, oldest first. */
    public List<Sale> sales() {
        return List.copyOf(sales);
    }

    /** The stock report text (same format as lesson 03). */
    public String report() {
        return StockReport.build(products());
    }

    /** Value of everything on the shelves, in centavos. */
    public int totalValue() {
        return StockReport.totalValue(products());
    }
}
