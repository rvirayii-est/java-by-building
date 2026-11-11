package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductDaoTest {

    private Database database;
    private ProductDao dao;

    @BeforeEach
    void open() {
        database = TestDb.fresh();
        dao = new ProductDao(database);
    }

    @AfterEach
    void close() {
        database.close();
    }

    @Test
    void aSavedProductCanBeFoundAgainWithAllItsFields() {
        dao.insert(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        Product found = dao.findByName("Rice 25kg").orElseThrow();
        assertEquals("Rice 25kg", found.getName());
        assertEquals(Money.of("1250.50"), found.getPrice());
        assertEquals(10, found.getReorderLevel());
        assertEquals(17, found.getQuantity());
    }

    @Test
    void findingIgnoresCapitalsAndExtraSpaces() {
        dao.insert(new Product("Rice 25kg", Money.of("1250.50"), 10));
        assertTrue(dao.findByName("  RICE   25kg ").isPresent());
    }

    @Test
    void anUnknownNameGivesAnEmptyOptional() {
        assertTrue(dao.findByName("salt").isEmpty());
    }

    @Test
    void theSameNameTwiceIsRefused() {
        dao.insert(new Product("Rice 25kg", Money.of("1250.50"), 10));
        InvalidInputException e = assertThrows(InvalidInputException.class,
                () -> dao.insert(new Product("rice 25KG", Money.of("1.00"), 1)));
        assertEquals("That product already exists: rice 25KG", e.getMessage());
        assertEquals(1, dao.count());
    }

    @Test
    void findAllKeepsTheOrderTheProductsWereAdded() {
        dao.insert(new Product("Sugar 1kg", Money.of("75.00"), 10));
        dao.insert(new Product("Rice 25kg", Money.of("1250.50"), 10));
        dao.insert(new Product("Cooking Oil 1L", Money.of("98.00"), 10));
        assertEquals(List.of("Sugar 1kg", "Rice 25kg", "Cooking Oil 1L"), dao.findAll().stream().map(Product::getName).toList());
    }

    @Test
    void updateChangesPriceReorderLevelAndQuantity() {
        dao.insert(new Product("Rice 25kg", Money.of("1250.50"), 10, 17));
        Product product = dao.findByName("Rice 25kg").orElseThrow();
        product.setPrice(Money.of("1300.00"));
        product.setReorderLevel(12);
        product.sell(5);
        dao.update(product);
        Product reloaded = dao.findByName("Rice 25kg").orElseThrow();
        assertEquals(Money.of("1300.00"), reloaded.getPrice());
        assertEquals(12, reloaded.getReorderLevel());
        assertEquals(12, reloaded.getQuantity());
    }

    @Test
    void updatingAProductThatWasNeverSavedIsAnError() {
        assertThrows(UnknownProductException.class, () -> dao.update(new Product("Ghost", Money.of("1.00"), 1)));
    }

    @Test
    void countCountsRows() {
        assertEquals(0, dao.count());
        dao.insert(new Product("A", Money.of("1.00"), 1));
        dao.insert(new Product("B", Money.of("1.00"), 1));
        assertEquals(2, dao.count());
    }

    @Test
    void textThatLooksLikeSqlIsJustTextBecauseOfPreparedStatements() throws SQLException {
        String attack = "x'); DROP TABLE product; --";
        dao.insert(new Product(attack, Money.of("5.00"), 1));
        assertEquals(attack, dao.findByName(attack).orElseThrow().getName()); // stored and found as plain text
        assertTrue(dao.findByName("' OR '1'='1").isEmpty());                  // no trick returns every row
        assertEquals(1, dao.count());                                           // the table is still there
    }

    @Test
    void gluingTextIntoSqlIsTheDangerousWayToDoIt() throws SQLException {
        dao.insert(new Product("Rice 25kg", Money.of("1250.50"), 10));
        dao.insert(new Product("Sugar 1kg", Money.of("75.00"), 10));
        String typedByAnAttacker = "nothing' OR '1'='1";
        // NEVER write code like this: the quote in the text ends the SQL text early and OR '1'='1' is always true.
        String glued = "SELECT COUNT(*) FROM product WHERE name = '" + typedByAnAttacker + "'";
        try (PreparedStatement statement = database.connection().prepareStatement(glued); ResultSet rows = statement.executeQuery()) {
            rows.next();
            assertEquals(2, rows.getInt(1)); // every product, although no product is called "nothing"
        }
    }
}
