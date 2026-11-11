package dev.javabybuilding.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class InputReaderTest {

    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(captured);

    private static Scanner typed(String... lines) {
        return new Scanner(String.join("\n", lines) + "\n");
    }

    @Test
    void readTextSkipsBlankLinesAndCleansTheAnswer() {
        String result = InputReader.readText(typed("   ", "  Rice   25kg "), out, "Name: ");
        assertEquals("Rice 25kg", result);
        assertTrue(captured.toString().contains("Please type something."));
    }

    @Test
    void readIntAcceptsAValidNumberFirstTime() {
        assertEquals(7, InputReader.readInt(typed(" 7 "), out, "Qty: ", 1, 100));
        assertEquals("Qty: ", captured.toString());
    }

    @Test
    void readIntAsksAgainForTextAndForNumbersOutOfRange() {
        int result = InputReader.readInt(typed("abc", "0", "500", "7"), out, "Qty: ", 1, 100);
        assertEquals(7, result);
        String output = captured.toString();
        assertTrue(output.contains("Please type a whole number."));
        assertTrue(output.contains("Please type a number from 1 to 100."));
    }

    @Test
    void readMoneyReturnsPesos() {
        assertEquals(Money.of("1250.50"), InputReader.readMoney(typed("1250.50"), out, "Price: ", Product.MAX_PRICE));
    }

    @Test
    void readMoneyAsksAgainForBadAmounts() {
        BigDecimal result = InputReader.readMoney(typed("12.345", "0", "20000", "9.50"), out, "Price: ", Product.MAX_PRICE);
        assertEquals(Money.of("9.50"), result);
        assertTrue(captured.toString().contains("up to PHP 10,000.00."));
    }

    @Test
    void whenTheInputEndsTheProgramStopsInsteadOfLoopingForever() {
        assertThrows(NoSuchElementException.class,
                () -> InputReader.readInt(typed("abc"), out, "Qty: ", 1, 100));
    }
}
