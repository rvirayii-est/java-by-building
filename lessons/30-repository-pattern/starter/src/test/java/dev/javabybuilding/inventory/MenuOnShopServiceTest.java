package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

/** The console app does not care whether its Shop is the in-memory Inventory or a ShopService on a database. */
class MenuOnShopServiceTest {

    private static String run(Shop shop, String typedInput) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        Main.run(new Scanner(typedInput), new PrintStream(captured), shop);
        return captured.toString();
    }

    private static ShopService serviceOnDatabase(Database database) {
        ShopService service = new ShopService(new JdbcProductRepository(database), new JdbcSaleRepository(database),
                new JdbcStockMovementRepository(database), Clock.system(Inventory.SHOP_ZONE));
        service.importFrom(Inventory.sample());
        return service;
    }

    // sell 2 rice; receive 5 sugar; add Salt; sell 1 salt (none in stock: refused); sell 99 rice (refused); report; quit
    private static final String SESSION = "3\nrice 25kg\n2\n2\nsugar 1kg\n5\n1\nSalt\n9.50\n10\n3\nsalt\n1\n3\nrice 25kg\n99\n4\n5\n";

    @Test
    void theSameKeystrokesGiveTheSameScreenOnInventoryAndOnTheDatabase() {
        String expected = run(Inventory.sample(), SESSION);
        try (Database database = TestDb.fresh()) {
            assertEquals(expected, run(serviceOnDatabase(database), SESSION));
        }
    }

    @Test
    void aSaleAtTheMenuIsSavedInTheDatabase() {
        try (Database database = TestDb.fresh()) {
            ShopService service = serviceOnDatabase(database);
            String screen = run(service, "3\nrice 25kg\n2\n5\n");
            assertTrue(screen.contains("Sold 2 x Rice 25kg = PHP 2,501.00"));
            assertEquals(10, new JdbcProductRepository(database).findByName("rice 25kg").orElseThrow().getQuantity());
            assertEquals(1, new JdbcSaleRepository(database).findAll().size());
        }
    }

    @Test
    void receivingStockShowsTheNewQuantityFromTheDatabase() {
        try (Database database = TestDb.fresh()) {
            String screen = run(serviceOnDatabase(database), "2\nsugar 1kg\n5\n5\n");
            assertTrue(screen.contains("Received 5 x Sugar 1kg. In stock now: 5"));
        }
    }
}
