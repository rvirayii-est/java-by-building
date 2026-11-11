package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Asks questions and keeps asking until the answer is valid.
 * Every method takes the Scanner and PrintStream as parameters, so tests can feed text in and read the output
 * without a keyboard. If the input runs out, Scanner throws NoSuchElementException and the program stops.
 */
public class InputReader {

    /**
     * TODO 3: ask until the person types something other than spaces; return the cleaned text.
     * Pattern: while (true) { out.print(prompt); String text = TextUtil.clean(in.nextLine());
     * if it is not empty, return it; otherwise out.println("Please type something."); }
     */
    public static String readText(Scanner in, PrintStream out, String prompt) {
        return "";
    }

    /**
     * TODO 4: ask until the person types a whole number from min to max (both allowed).
     * Not a whole number (Validation.isWholeNumber): print "Please type a whole number."
     * Too small or too big: print "Please type a number from " + min + " to " + max + "." and ask again.
     * Use Integer.parseInt(text.trim()) only after isWholeNumber says it is safe.
     */
    public static int readInt(Scanner in, PrintStream out, String prompt, int min, int max) {
        return 0;
    }

    /**
     * TODO 5: ask for an amount in pesos; return centavos from 1 up to maxCentavos.
     * Use Validation.parseCentavos. When it is not valid (-1) or out of range, print
     * "Please type an amount like 125 or 125.50, up to " + PriceCalculator.format(maxCentavos) + "." and ask again.
     */
    public static int readMoney(Scanner in, PrintStream out, String prompt, int maxCentavos) {
        return 0;
    }
}
