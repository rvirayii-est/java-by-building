package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileProductRepositoryTest extends ProductRepositoryContract {

    @TempDir
    Path folder;

    @Override
    ProductRepository create() {
        return new FileProductRepository(folder);
    }

    @Test
    void theFileUsesTheSameFormatAsDataStore() throws IOException {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        assertEquals(List.of("name,price,reorderLevel,quantity", "Rice 25kg,1250.50,10,17"), Files.readAllLines(folder.resolve("products.csv")));
    }

    @Test
    void aFileSavedByDataStoreCanBeReadByTheRepository() throws IOException {
        DataStore.save(Inventory.sample(), folder);
        List<Product> products = new FileProductRepository(folder).findAll();
        assertEquals(4, products.size());
        assertEquals(12, new FileProductRepository(folder).findByName("rice 25kg").orElseThrow().getQuantity());
    }

    @Test
    void anotherRepositoryOnTheSameFolderSeesTheSameProducts() {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        assertTrue(new FileProductRepository(folder).findByName("Rice 25kg").isPresent());
    }

    @Test
    void noTemporaryFileIsLeftBehind() throws IOException {
        repository.add(new Product("Rice 25kg", Money.of("1250.50"), 10));
        try (var files = Files.list(folder)) {
            assertEquals(List.of("products.csv"), files.map(p -> p.getFileName().toString()).toList());
        }
    }
}
