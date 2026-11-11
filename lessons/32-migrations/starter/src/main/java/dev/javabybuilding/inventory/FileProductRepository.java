package dev.javabybuilding.inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Products kept in products.csv, the same file DataStore (lesson 19) writes, so the two can read each other's files.
 * Proof that the repository pattern is real: this class replaces JdbcProductRepository without the app noticing.
 *
 * <p>shortcut: every call reads or rewrites the whole file. That is fine for a few hundred products and one user,
 * and it is exactly the kind of limit that makes people move to a database.
 */
public class FileProductRepository implements ProductRepository {

    private static final String HEADER = "name,price,reorderLevel,quantity"; // the header DataStore uses
    private final Path file;

    /** The file is dir/products.csv; it is created when the first product is added. */
    public FileProductRepository(Path dir) {
        this.file = dir.resolve("products.csv");
    }

    @Override
    public void add(Product product) {
        List<Product> products = readAll();
        if (find(products, product.getName()).isPresent()) {
            throw new InvalidInputException("That product already exists: " + product.getName());
        }
        products.add(product);
        writeAll(products);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return find(readAll(), name);
    }

    @Override
    public List<Product> findAll() {
        return readAll();
    }

    @Override
    public void update(Product product) {
        List<Product> products = readAll();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equals(product.getName())) {
                products.set(i, product);
                writeAll(products);
                return;
            }
        }
        throw new UnknownProductException(product.getName());
    }

    private static Optional<Product> find(List<Product> products, String name) {
        String wanted = TextUtil.clean(name).toLowerCase();
        return products.stream().filter(p -> p.getName().toLowerCase().equals(wanted)).findFirst();
    }

    private List<Product> readAll() {
        List<Product> products = new ArrayList<>();
        if (!Files.exists(file)) {
            return products;
        }
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 1; i < lines.size(); i++) { // line 0 is the header
                if (lines.get(i).isBlank()) {
                    continue;
                }
                List<String> f = Csv.parseLine(lines.get(i));
                products.add(new Product(f.get(0), Money.of(f.get(1)), Integer.parseInt(f.get(2)), Integer.parseInt(f.get(3))));
            }
            return products;
        } catch (IOException e) {
            throw new DataFileException("Cannot read " + file + ": " + e.getMessage());
        } catch (RuntimeException e) {
            if (e instanceof ShopException) {
                throw e;
            }
            throw new DataFileException(file.getFileName() + " is not a valid products file: " + e.getMessage());
        }
    }

    private void writeAll(List<Product> products) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Product p : products) {
            lines.add(Csv.join(List.of(p.getName(), p.getPrice().toPlainString(),
                    String.valueOf(p.getReorderLevel()), String.valueOf(p.getQuantity()))));
        }
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            Path temporary = file.resolveSibling(file.getFileName() + ".tmp"); // write whole, then move: never a half file
            Files.write(temporary, lines);
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DataFileException("Cannot write " + file + ": " + e.getMessage());
        }
    }
}
