package dev.javabybuilding.inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Saves the whole shop to three CSV files in a folder, and reads it back:
 * <pre>
 * products.csv   name,priceCentavos,reorderLevel,quantity
 * sales.csv      number,customer,phone,discount,payment,product,unitPriceCentavos,quantity   (one row per sale line)
 * movements.csv  product,type,quantity
 * </pre>
 * Every method takes the folder as a parameter, so tests can use a temporary folder and never touch real data.
 * Problems opening or writing a file are reported with the checked IOException; problems INSIDE a file with
 * DataFileException (which names the file and the line).
 */
public class DataStore {

    static final String PRODUCTS = "products.csv";
    static final String SALES = "sales.csv";
    static final String MOVEMENTS = "movements.csv";

    private static final String PRODUCTS_HEADER = "name,priceCentavos,reorderLevel,quantity";
    private static final String SALES_HEADER = "number,customer,phone,discount,payment,product,unitPriceCentavos,quantity";
    private static final String MOVEMENTS_HEADER = "product,type,quantity";

    /** True when dir already holds a saved shop (a products.csv file). */
    public static boolean exists(Path dir) {
        return Files.exists(dir.resolve(PRODUCTS));
    }

    // ---------------------------------------------------------------- saving

    /** Writes the three files into dir, creating the folder if needed. An old save in dir is replaced. */
    public static void save(Inventory inventory, Path dir) throws IOException {
        Files.createDirectories(dir);
        saveProducts(inventory, dir.resolve(PRODUCTS));
        saveSales(inventory, dir.resolve(SALES));
        saveMovements(inventory, dir.resolve(MOVEMENTS));
    }

    private static void saveProducts(Inventory inventory, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(PRODUCTS_HEADER);
        // TODO 4a: for every product add  Csv.join(List.of(name, price, reorder level, quantity))  as text (String.valueOf(number)).
        // Copy the pattern of saveSales below.
        writeLines(file, lines);
    }

    private static void saveSales(Inventory inventory, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(SALES_HEADER);
        for (Sale sale : inventory.sales()) {
            Customer customer = sale.getCustomer();
            String name = customer.isWalkIn() ? "" : customer.getName(); // an empty name means the walk-in customer
            for (SaleLine line : sale.getLines()) {
                lines.add(Csv.join(List.of(String.valueOf(sale.getNumber()), name, customer.getPhone(),
                        DiscountCodes.encode(sale.getDiscount()), sale.getPayment().name(), line.productName(),
                        String.valueOf(line.unitPriceCentavos()), String.valueOf(line.quantity()))));
            }
        }
        writeLines(file, lines);
    }

    private static void saveMovements(Inventory inventory, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(MOVEMENTS_HEADER);
        // TODO 4b: for every movement add  Csv.join(List.of(productName, type.name(), quantity as text)).
        writeLines(file, lines);
    }

    /**
     * Writes to a temporary file first, then moves it over the real one. If the program dies half way through writing,
     * the old file is still whole instead of cut off in the middle.
     */
    private static void writeLines(Path file, List<String> lines) throws IOException {
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        Files.write(temporary, lines); // UTF-8, one line each
        Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
    }

    // ---------------------------------------------------------------- loading

    /**
     * Reads the shop saved in dir.
     *
     * @throws IOException when a file is missing or cannot be read (NoSuchFileException is one kind of IOException)
     * @throws DataFileException when a file's contents are wrong, or the files disagree with each other
     */
    public static Inventory load(Path dir) throws IOException {
        List<Product> products = readRows(dir.resolve(PRODUCTS), PRODUCTS_HEADER, DataStore::toProduct);
        List<StockMovement> movements = readRows(dir.resolve(MOVEMENTS), MOVEMENTS_HEADER, DataStore::toMovement);
        List<SaleRow> rows = readRows(dir.resolve(SALES), SALES_HEADER, DataStore::toSaleRow);
        Inventory inventory = Inventory.restore(products, buildSales(rows), movements);
        checkShelvesMatchLog(inventory);
        return inventory;
    }

    /** One line of sales.csv before the lines are grouped back into sales. */
    private record SaleRow(int number, String customer, String phone, Discount discount, PaymentMethod payment, SaleLine line) {}

    /**
     * Reads a CSV file: checks the header, then turns every other line into an object with rowToObject.
     * T is whatever kind of object the file holds. Any ShopException thrown for one row is reported with the file and line.
     */
    private static <T> List<T> readRows(Path file, String header, Function<List<String>, T> rowToObject) throws IOException {
        String fileName = file.getFileName().toString();
        List<String> lines = Files.readAllLines(file); // throws NoSuchFileException when the file is not there
        if (lines.isEmpty() || !lines.get(0).equals(header)) {
            throw new DataFileException(fileName, 1, "expected the header " + header);
        }
        int columns = header.split(",").length;
        List<T> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            int lineNumber = i + 1; // people count lines from 1, and line 1 was the header
            if (lines.get(i).isBlank()) {
                continue;
            }
            try {
                List<String> fields = Csv.parseLine(lines.get(i));
                if (fields.size() != columns) {
                    throw new InvalidInputException("expected " + columns + " columns but found " + fields.size());
                }
                rows.add(rowToObject.apply(fields));
            } catch (ShopException e) {
                throw new DataFileException(fileName, lineNumber, e.getMessage());
            }
        }
        return rows;
    }

    private static Product toProduct(List<String> f) {
        // TODO 5a: the four columns are name, price, reorder level and quantity:
        // return new Product(f.get(0), wholeNumber(f.get(1)), wholeNumber(f.get(2)), wholeNumber(f.get(3)));
        throw new UnsupportedOperationException("TODO 5a");
    }

    private static StockMovement toMovement(List<String> f) {
        // TODO 5b: columns are product, type, quantity. Turn the type text into a MovementType with MovementType.valueOf(...)
        // inside try/catch (IllegalArgumentException e) { throw new InvalidInputException("unknown movement type: " + f.get(1)); }
        // (see toSaleRow below for the same idea with PaymentMethod), then return new StockMovement(f.get(0), type, wholeNumber(f.get(2))).
        throw new UnsupportedOperationException("TODO 5b");
    }

    private static SaleRow toSaleRow(List<String> f) {
        PaymentMethod payment;
        try {
            payment = PaymentMethod.valueOf(f.get(4));
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("unknown payment method: " + f.get(4));
        }
        SaleLine line = new SaleLine(f.get(5), wholeNumber(f.get(6)), wholeNumber(f.get(7)));
        return new SaleRow(wholeNumber(f.get(0)), f.get(1), f.get(2), DiscountCodes.decode(f.get(3)), payment, line);
    }

    private static int wholeNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("not a whole number: " + text);
        }
    }

    /**
     * Groups the rows back into sales: all rows with the same number belong to one Sale. The same customer in two sales
     * must be ONE Customer object (so the points add up), found by name and phone. Points are replayed, not stored:
     * they are worked out again from the sales, in order.
     */
    private static List<Sale> buildSales(List<SaleRow> rows) {
        Map<Integer, Sale> sales = new LinkedHashMap<>(); // keeps the order of first appearance
        Map<String, Customer> customers = new LinkedHashMap<>();
        for (SaleRow row : rows) {
            Sale sale = sales.get(row.number());
            if (sale == null) {
                Customer customer = Customer.WALK_IN;
                if (!row.customer().isEmpty()) {
                    customer = customers.computeIfAbsent(row.customer() + "|" + row.phone(), k -> new Customer(row.customer(), row.phone()));
                }
                sale = new Sale(row.number(), customer);
                sale.setDiscount(row.discount());
                sale.setPayment(row.payment());
                sales.put(row.number(), sale);
            }
            sale.addLine(row.line());
        }
        for (Sale sale : sales.values()) {
            sale.getCustomer().addPurchase(sale.total()); // the walk-in customer ignores this
        }
        return new ArrayList<>(sales.values());
    }

    /** The shelf quantity in products.csv must equal received minus sold in movements.csv; if not, one file was edited by hand. */
    private static void checkShelvesMatchLog(Inventory inventory) {
        for (Product product : inventory.products()) {
            int logged = inventory.netMovement(product.getName());
            if (logged != product.getQuantity()) {
                throw new DataFileException(PRODUCTS + " says " + product.getName() + " has " + product.getQuantity()
                        + " in stock, but " + MOVEMENTS + " adds up to " + logged + ".");
            }
        }
    }
}
