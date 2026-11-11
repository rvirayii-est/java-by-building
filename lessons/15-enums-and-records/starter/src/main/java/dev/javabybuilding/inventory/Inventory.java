package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * The shop's stock: one ArrayList of Product objects. Products are now encapsulated (private fields), so this class
 * talks to them only through their methods. It also keeps the list of Sales (receipts). The older methods still
 * behave exactly as before.
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
     * Adds a new product with 0 in stock. Returns false (and changes nothing) when the name is blank, the product
     * already exists, the price is not above 0, or the reorder level is below 0.
     */
    public boolean addProduct(String name, int priceCentavos, int reorderLevel) {
        String clean = TextUtil.clean(name);
        if (clean.isEmpty() || findProduct(clean) != null || !Product.isValidPrice(priceCentavos)
                || !Product.isValidReorderLevel(reorderLevel)) {
            return false;
        }
        products.add(new Product(clean, priceCentavos, reorderLevel));
        return true;
    }

    /** Units in stock, or -1 when there is no such product. */
    public int quantityOf(String name) {
        Product product = findProduct(name);
        if (product == null) {
            return -1;
        }
        return product.getQuantity();
    }

    /** Unit price in centavos, or -1 when there is no such product. */
    public int priceOf(String name) {
        Product product = findProduct(name);
        if (product == null) {
            return -1;
        }
        return product.getPriceCentavos();
    }

    /** Adds amount units. Returns false (and changes nothing) for an unknown product or an amount of 0 or less. */
    public boolean receive(String name, int amount) {
        Product product = findProduct(name);
        if (product == null || !product.receive(amount)) { // Product refuses amounts of 0 or less itself
            return false;
        }
        // TODO 4a: log it: movements.add(new StockMovement(product.getName(), MovementType.RECEIVED, amount));
        return true;
    }

    /** Changes a product's price. Returns false (and changes nothing) for an unknown product or a price of 0 or less. */
    public boolean changePrice(String name, int newPriceCentavos) {
        Product product = findProduct(name);
        return product != null && product.setPrice(newPriceCentavos);
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
     * Sells amount units to a customer with a discount and a payment method. Records a Sale (number, customer, one line,
     * discount, payment) and a SOLD stock movement, gives
     * the customer points for the discounted total, and returns the Sale. Returns null and changes nothing when the
     * product is unknown, the amount is 0 or less, or there is not enough stock. (Lesson 16 replaces the null with an
     * exception.) The Inventory does not care WHICH kind of Discount it gets: any object that implements the interface works.
     */
    public Sale sell(String name, int amount, Customer customer, Discount discount, PaymentMethod payment) {
        Product product = findProduct(name);
        if (product == null || !product.sell(amount)) { // Product refuses 0 or less, and not enough stock
            return null;
        }
        Sale sale = new Sale(sales.size() + 1, customer);
        sale.addLine(new SaleLine(product.getName(), product.getPriceCentavos(), amount));
        sale.setDiscount(discount);
        sale.setPayment(payment);
        customer.addPurchase(sale.total());
        sales.add(sale);
        // TODO 4b: log it: movements.add(new StockMovement(product.getName(), MovementType.SOLD, amount));
        return sale;
    }

    /**
     * Sells amount units to an anonymous walk-in customer. Returns the sale total in centavos (unit price x amount),
     * or -1 and changes nothing when the product is unknown, the amount is 0 or less, or there is not enough stock.
     */
    public int sell(String name, int amount) {
        Sale sale = sell(name, amount, Customer.WALK_IN);
        if (sale == null) {
            return -1;
        }
        return sale.total();
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
     * Units received minus units sold for one product, according to the log, or -1 for an unknown product. Because a
     * product starts with 0 in stock, this always equals quantityOf(name): the log and the shelf agree.
     */
    public int netMovement(String name) {
        // TODO 4c: return -1 when find(name) is null. Otherwise add up movement.signedQuantity() for every movement
        // whose productName() equals the stored name, and return the total.
        return 0;
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
