package dev.javabybuilding.inventory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Saves the whole shop to three CSV files in a folder, and reads it back:
 * <pre>
 * products.csv   name,price,reorderLevel,quantity                               price is pesos, like 1250.50
 * sales.csv      number,dateTime,customer,phone,discount,payment,product,unitPrice,quantity   (one row per sale line)
 * movements.csv  product,type,quantity,dateTime                                  dateTime is like 2026-03-01T09:30:00
 * </pre>
 * Every method takes the folder as a parameter, so tests can use a temporary folder and never touch real data.
 * Problems opening or writing a file are reported with the checked IOException; problems INSIDE a file with
 * DataFileException (which names the file and the line).
 *
 * <p>Lesson 20 changed the format (pesos instead of centavos, and times were added). Files saved by lesson 19 are
 * still understood: they are recognised by their old header and every row is upgraded as it is read. Code changes
 * quickly; saved files live for years, so a loader keeps reading the old formats.
 */
public class DataStore {

    static final String PRODUCTS = "products.csv";
    static final String SALES = "sales.csv";
    static final String MOVEMENTS = "movements.csv";

    private static final String PRODUCTS_HEADER = "name,price,reorderLevel,quantity";
    private static final String SALES_HEADER = "number,dateTime,customer,phone,discount,payment,product,unitPrice,quantity";
    private static final String MOVEMENTS_HEADER = "product,type,quantity,dateTime";

    // The lesson 19 formats (money in centavos, no times).
    private static final String LEGACY_PRODUCTS_HEADER = "name,priceCentavos,reorderLevel,quantity";
    private static final String LEGACY_SALES_HEADER = "number,customer,phone,discount,payment,product,unitPriceCentavos,quantity";
    private static final String LEGACY_MOVEMENTS_HEADER = "product,type,quantity";

    /**
     * The old files did not record when anything happened, and we will not invent a time. Upgraded rows get this
     * obvious "unknown" date, 1 January 1970, so nobody mistakes them for real timestamps.
     */
    public static final LocalDateTime UNKNOWN_TIME = LocalDateTime.of(1970, 1, 1, 0, 0);

    /** Writes 2026-03-01T09:30:00 (always with seconds, so every line looks the same). */
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
        for (Product product : inventory.products()) {
            lines.add(Csv.join(List.of(product.getName(), product.getPrice().toPlainString(), // plain: 1250.50, never 1.2505E+3
                    String.valueOf(product.getReorderLevel()), String.valueOf(product.getQuantity()))));
        }
        writeLines(file, lines);
    }

    private static void saveSales(Inventory inventory, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(SALES_HEADER);
        for (Sale sale : inventory.sales()) {
            Customer customer = sale.getCustomer();
            String name = customer.isWalkIn() ? "" : customer.getName(); // an empty name means the walk-in customer
            for (SaleLine line : sale.getLines()) {
                lines.add(Csv.join(List.of(String.valueOf(sale.getNumber()), FILE_TIME.format(sale.getDateTime()), name,
                        customer.getPhone(), DiscountCodes.encode(sale.getDiscount()), sale.getPayment().name(),
                        line.productName(), line.unitPrice().toPlainString(), String.valueOf(line.quantity()))));
            }
        }
        writeLines(file, lines);
    }

    private static void saveMovements(Inventory inventory, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(MOVEMENTS_HEADER);
        for (StockMovement movement : inventory.movements()) {
            lines.add(Csv.join(List.of(movement.productName(), movement.type().name(), String.valueOf(movement.quantity()),
                    FILE_TIME.format(movement.at()))));
        }
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

    /** Reads the shop saved in dir; its new sales and movements will be stamped by the real clock in the shop's time zone. */
    public static Inventory load(Path dir) throws IOException {
        return load(dir, Clock.system(Inventory.SHOP_ZONE));
    }

    /**
     * Reads the shop saved in dir. The Clock is for the loaded inventory's FUTURE sales; times in the files are kept as written.
     *
     * @throws IOException when a file is missing or cannot be read (NoSuchFileException is one kind of IOException)
     * @throws DataFileException when a file's contents are wrong, or the files disagree with each other
     */
    public static Inventory load(Path dir, Clock clock) throws IOException {
        List<Product> products = readRows(dir.resolve(PRODUCTS), PRODUCTS_HEADER, LEGACY_PRODUCTS_HEADER,
                DataStore::upgradeProductRow, DataStore::toProduct);
        List<StockMovement> movements = readRows(dir.resolve(MOVEMENTS), MOVEMENTS_HEADER, LEGACY_MOVEMENTS_HEADER,
                DataStore::upgradeMovementRow, DataStore::toMovement);
        List<SaleRow> rows = readRows(dir.resolve(SALES), SALES_HEADER, LEGACY_SALES_HEADER,
                DataStore::upgradeSaleRow, DataStore::toSaleRow);
        Inventory inventory = Inventory.restore(products, buildSales(rows), movements, clock);
        checkShelvesMatchLog(inventory);
        return inventory;
    }

    /** One line of sales.csv before the lines are grouped back into sales. */
    private record SaleRow(int number, LocalDateTime at, String customer, String phone, Discount discount,
                           PaymentMethod payment, SaleLine line) {}

    /**
     * Reads a CSV file: checks the header, then turns every other line into an object with rowToObject.
     * T is whatever kind of object the file holds. A file with the legacy header has each row rewritten into the
     * current layout by upgradeLegacyRow first. Any ShopException thrown for one row is reported with the file and line.
     */
    private static <T> List<T> readRows(Path file, String header, String legacyHeader,
                                        UnaryOperator<List<String>> upgradeLegacyRow,
                                        Function<List<String>, T> rowToObject) throws IOException {
        String fileName = file.getFileName().toString();
        List<String> lines = Files.readAllLines(file); // throws NoSuchFileException when the file is not there
        boolean legacy = !lines.isEmpty() && lines.get(0).equals(legacyHeader);
        if (lines.isEmpty() || (!legacy && !lines.get(0).equals(header))) {
            throw new DataFileException(fileName, 1, "expected the header " + header);
        }
        int columns = (legacy ? legacyHeader : header).split(",").length;
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
                rows.add(rowToObject.apply(legacy ? upgradeLegacyRow.apply(fields) : fields));
            } catch (ShopException e) {
                throw new DataFileException(fileName, lineNumber, e.getMessage());
            }
        }
        return rows;
    }

    // ---- the lesson 19 formats, rewritten into today's layout (still as text; the toXxx methods build the objects)

    /** "125050" centavos becomes "1250.50" pesos: a BigDecimal made from the digits 125050 with the point 2 places from the right. */
    static String centavosToPesos(String centavosText) {
        // TODO 7: BigDecimal.valueOf(unscaledValue, scale) puts the decimal point `scale` places from the right:
        // return BigDecimal.valueOf(wholeNumber(centavosText), 2).toPlainString();
        return "0.00";
    }

    private static List<String> upgradeProductRow(List<String> f) {
        return List.of(f.get(0), centavosToPesos(f.get(1)), f.get(2), f.get(3));
    }

    private static List<String> upgradeMovementRow(List<String> f) {
        return List.of(f.get(0), f.get(1), f.get(2), FILE_TIME.format(UNKNOWN_TIME));
    }

    private static List<String> upgradeSaleRow(List<String> f) {
        String discount = f.get(3).startsWith("fixed:") ? "fixed:" + centavosToPesos(f.get(3).substring("fixed:".length())) : f.get(3);
        return List.of(f.get(0), FILE_TIME.format(UNKNOWN_TIME), f.get(1), f.get(2), discount, f.get(4), f.get(5),
                centavosToPesos(f.get(6)), f.get(7));
    }

    // ---- one row of text into one object

    private static Product toProduct(List<String> f) {
        return new Product(f.get(0), money(f.get(1)), wholeNumber(f.get(2)), wholeNumber(f.get(3)));
    }

    private static StockMovement toMovement(List<String> f) {
        MovementType type;
        try {
            type = MovementType.valueOf(f.get(1));
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("unknown movement type: " + f.get(1));
        }
        return new StockMovement(f.get(0), type, wholeNumber(f.get(2)), dateTime(f.get(3)));
    }

    private static SaleRow toSaleRow(List<String> f) {
        PaymentMethod payment;
        try {
            payment = PaymentMethod.valueOf(f.get(5));
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("unknown payment method: " + f.get(5));
        }
        SaleLine line = new SaleLine(f.get(6), money(f.get(7)), wholeNumber(f.get(8)));
        return new SaleRow(wholeNumber(f.get(0)), dateTime(f.get(1)), f.get(2), f.get(3), DiscountCodes.decode(f.get(4)), payment, line);
    }

    private static int wholeNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("not a whole number: " + text);
        }
    }

    private static BigDecimal money(String text) {
        try {
            return Money.of(text);
        } catch (NumberFormatException | ArithmeticException e) { // not a number, or more than 2 decimals
            throw new InvalidInputException("not an amount of pesos: " + text);
        }
    }

    private static LocalDateTime dateTime(String text) {
        try {
            return LocalDateTime.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("not a date and time like 2026-03-01T09:30:00: " + text);
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
                sale = new Sale(row.number(), customer, row.at());
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
