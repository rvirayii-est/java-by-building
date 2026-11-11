package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * The shop's stock: a list of Products, a list of Sales (receipts) and the stock log. Problems are reported by
 * throwing exceptions (see ShopException), not by returning -1, false or null as lessons 07-15 did.
 * Money is whole centavos (int), as always.
 */
public class Inventory {

    private final ArrayList<Product> products = new ArrayList<>();
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
        for (Product product : products) {
            if (TextUtil.sameName(product.getName(), name)) {
                return product;
            }
        }
        return null;
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
        products.add(new Product(clean, priceCentavos, reorderLevel)); // the Product constructor checks the rest
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
        return List.copyOf(products);
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
        int total = 0;
        for (Sale sale : sales) {
            total += sale.total();
        }
        return total;
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

    /** A snapshot of every sale made so far, oldest first. */
    public List<Sale> sales() {
        return List.copyOf(sales);
    }

    /** The stock report text (same format as lesson 03). */
    public String report() {
        return StockReport.build(products);
    }

    /** Value of everything on the shelves, in centavos. */
    public int totalValue() {
        return StockReport.totalValue(products);
    }
}
