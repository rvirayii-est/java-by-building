package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** @TempDir gives every test its own empty folder and deletes it afterwards, so no test leaves files in the repo. */
class DataStoreTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE); // 09:30 in Manila

    @TempDir
    Path dir;

    private Inventory busyShop() {
        Inventory inventory = Inventory.sample();
        Customer ana = new Customer("Ana Reyes", "0917");
        inventory.sell("Rice 25kg", 2, ana, new PercentDiscount(10), PaymentMethod.GCASH);
        inventory.sell("Sardines 155g", 10, ana);
        inventory.sell("Cooking Oil 1L", 1); // walk-in
        inventory.sell("Sardines 155g", 3, new Customer("Ben Cruz", "0918"), new FixedDiscount(Money.of("5.00")), PaymentMethod.CARD);
        return inventory;
    }

    @Test
    void savingWritesThreeFilesWithHeaders() throws IOException {
        DataStore.save(Inventory.sample(new Inventory(CLOCK)), dir);
        List<String> products = Files.readAllLines(dir.resolve("products.csv"));
        assertEquals("name,price,reorderLevel,quantity", products.get(0));
        assertEquals("Rice 25kg,1250.50,10,12", products.get(1));
        assertEquals(5, products.size()); // header + 4 products
        assertTrue(Files.exists(dir.resolve("sales.csv")));
        assertEquals(List.of("product,type,quantity,dateTime", "Rice 25kg,RECEIVED,12,2026-03-01T09:30:00",
                "Cooking Oil 1L,RECEIVED,4,2026-03-01T09:30:00", "Sardines 155g,RECEIVED,60,2026-03-01T09:30:00"),
                Files.readAllLines(dir.resolve("movements.csv")));
    }

    @Test
    void whatYouSaveIsWhatYouLoad() throws IOException {
        Inventory before = busyShop();
        DataStore.save(before, dir);
        Inventory after = DataStore.load(dir);

        assertEquals(before.products().stream().map(Product::toString).toList(), after.products().stream().map(Product::toString).toList());
        assertEquals(before.movements(), after.movements()); // records compare by value
        assertEquals(before.salesTotal(), after.salesTotal());
        assertEquals(before.report(), after.report());
        assertEquals(4, after.sales().size());
        assertEquals("GCASH", after.sales().get(0).getPayment().name());
        assertEquals("10% off", after.sales().get(0).getDiscount().label());
    }

    @Test
    void theSameCustomerInTwoSalesIsOneCustomerWithTheirPointsReplayed() throws IOException {
        DataStore.save(busyShop(), dir);
        Inventory after = DataStore.load(dir);
        Customer first = after.sales().get(0).getCustomer();
        Customer second = after.sales().get(1).getCustomer();
        assertEquals("Ana Reyes", first.getName());
        assertTrue(first == second, "one Customer object, so points add up");
        assertEquals(24, first.getPoints()); // sale 1: PHP 2,250.90 is 22 points; sale 2: PHP 240.00 is 2 points
        assertTrue(after.sales().get(2).getCustomer().isWalkIn());
    }

    @Test
    void namesWithCommasAndQuotesSurvive() throws IOException {
        Inventory inventory = new Inventory();
        inventory.addProduct("Rice, premium \"5kg\"", Money.of("1000.00"), 5);
        inventory.receive("Rice, premium \"5kg\"", 7);
        DataStore.save(inventory, dir);
        assertEquals("\"Rice, premium \"\"5kg\"\"\",1000.00,5,7", Files.readAllLines(dir.resolve("products.csv")).get(1));
        assertEquals(7, DataStore.load(dir).quantityOf("Rice, premium \"5kg\""));
    }

    @Test
    void savingCreatesTheFolderAndReplacesAnOldSave() throws IOException {
        Path nested = dir.resolve("a").resolve("b");
        DataStore.save(Inventory.sample(), nested);
        Inventory changed = Inventory.sample();
        changed.sell("Rice 25kg", 5);
        DataStore.save(changed, nested);
        assertEquals(7, DataStore.load(nested).quantityOf("Rice 25kg"));
        try (var files = Files.list(nested)) {
            assertEquals(3, files.count(), "no leftover .tmp files");
        }
    }

    @Test
    void existsIsTrueOnlyAfterASave() throws IOException {
        assertFalse(DataStore.exists(dir));
        DataStore.save(Inventory.sample(), dir);
        assertTrue(DataStore.exists(dir));
    }

    @Test
    void loadingFromAFolderWithoutFilesThrowsIOException() {
        assertThrows(NoSuchFileException.class, () -> DataStore.load(dir));
    }

    @Test
    void aWrongHeaderIsReported() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("products.csv"), "name,price\nRice,100\n");
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("products.csv, line 1: expected the header name,price,reorderLevel,quantity", e.getMessage());
    }

    @Test
    void aBadNumberNamesTheFileAndTheLine() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("products.csv"), "name,price,reorderLevel,quantity\nRice 25kg,1250.50,10,12\nSugar 1kg,seventy five,10,0\n");
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("products.csv, line 3: not an amount of pesos: seventy five", e.getMessage());
    }

    @Test
    void aRowWithTheWrongNumberOfColumnsIsReported() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("movements.csv"), "product,type,quantity,dateTime\nRice 25kg,RECEIVED\n");
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("movements.csv, line 2: expected 4 columns but found 2", e.getMessage());
    }

    @Test
    void anUnknownPaymentMethodIsReported() throws IOException {
        DataStore.save(busyShop(), dir);
        String text = Files.readString(dir.resolve("sales.csv")).replace("GCASH", "BITCOIN");
        Files.writeString(dir.resolve("sales.csv"), text);
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("sales.csv, line 2: unknown payment method: BITCOIN", e.getMessage());
    }

    @Test
    void filesThatDisagreeAreRefused() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("products.csv"), "name,price,reorderLevel,quantity\nRice 25kg,1250.50,10,99\n");
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("products.csv says Rice 25kg has 99 in stock, but movements.csv adds up to 12.", e.getMessage());
    }

    @Test
    void timesAndPesosRoundTripExactly() throws IOException {
        TestClock clock = new TestClock(Instant.parse("2026-03-01T01:30:00Z"), Inventory.SHOP_ZONE);
        Inventory before = Inventory.sample(new Inventory(clock));
        clock.advance(Duration.ofMinutes(95));
        before.sell("Rice 25kg", 2, new Customer("Ana Reyes", "0917"), new FixedDiscount(Money.of("50.50")), PaymentMethod.GCASH);
        DataStore.save(before, dir);

        assertEquals("1,2026-03-01T11:05:00,Ana Reyes,0917,fixed:50.50,GCASH,Rice 25kg,1250.50,2", Files.readAllLines(dir.resolve("sales.csv")).get(1));
        Inventory after = DataStore.load(dir, clock);
        assertEquals(LocalDateTime.of(2026, 3, 1, 11, 5), after.sales().get(0).getDateTime());
        assertEquals(Money.of("2450.50"), after.sales().get(0).total()); // 2501.00 less 50.50
        assertEquals(before.movements(), after.movements());
    }

    @Test
    void filesSavedByLesson19AreStillReadAndUpgraded() throws IOException {
        Files.writeString(dir.resolve("products.csv"), "name,priceCentavos,reorderLevel,quantity\nRice 25kg,125050,10,10\nSugar 1kg,7505,10,0\n");
        Files.writeString(dir.resolve("movements.csv"), "product,type,quantity\nRice 25kg,RECEIVED,12\nRice 25kg,SOLD,2\n");
        Files.writeString(dir.resolve("sales.csv"), "number,customer,phone,discount,payment,product,unitPriceCentavos,quantity\n"
                + "1,Ana Reyes,0917,fixed:5000,GCASH,Rice 25kg,125050,2\n");

        Inventory shop = DataStore.load(dir);

        assertEquals(Money.of("1250.50"), shop.priceOf("Rice 25kg")); // 125050 centavos became 1250.50 pesos
        assertEquals(Money.of("75.05"), shop.priceOf("Sugar 1kg"));
        assertEquals(Money.of("2451.00"), shop.sales().get(0).total()); // 2501.00 less the coupon, now 50.00 pesos
        assertEquals("PHP 50.00 off", shop.sales().get(0).getDiscount().label());
        assertEquals(DataStore.UNKNOWN_TIME, shop.movements().get(0).at()); // the old files had no times, and we do not invent one
        assertEquals(DataStore.UNKNOWN_TIME, shop.sales().get(0).getDateTime());
        assertEquals(24, shop.sales().get(0).getCustomer().getPoints());
    }

    @Test
    void anUpgradedShopIsSavedInTheNewFormat() throws IOException {
        Files.writeString(dir.resolve("products.csv"), "name,priceCentavos,reorderLevel,quantity\nRice 25kg,125050,10,0\n");
        Files.writeString(dir.resolve("movements.csv"), "product,type,quantity\n");
        Files.writeString(dir.resolve("sales.csv"), "number,customer,phone,discount,payment,product,unitPriceCentavos,quantity\n");
        DataStore.save(DataStore.load(dir), dir);
        assertEquals(List.of("name,price,reorderLevel,quantity", "Rice 25kg,1250.50,10,0"), Files.readAllLines(dir.resolve("products.csv")));
    }

    @Test
    void aBadDateTimeIsReported() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("movements.csv"), "product,type,quantity,dateTime\nRice 25kg,RECEIVED,12,yesterday\n");
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("movements.csv, line 2: not a date and time like 2026-03-01T09:30:00: yesterday", e.getMessage());
    }

    @Test
    void anAmountWithThreeDecimalsIsRefusedNotRounded() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("products.csv"), "name,price,reorderLevel,quantity\nRice 25kg,1250.505,10,12\n");
        DataFileException e = assertThrows(DataFileException.class, () -> DataStore.load(dir));
        assertEquals("products.csv, line 2: not an amount of pesos: 1250.505", e.getMessage());
    }

    @Test
    void blankLinesAreSkipped() throws IOException {
        DataStore.save(Inventory.sample(), dir);
        Files.writeString(dir.resolve("sales.csv"), "number,dateTime,customer,phone,discount,payment,product,unitPrice,quantity\n\n");
        assertEquals(0, DataStore.load(dir).sales().size());
    }
}
