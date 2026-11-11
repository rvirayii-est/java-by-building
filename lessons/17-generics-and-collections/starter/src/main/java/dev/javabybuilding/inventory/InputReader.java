package dev.javabybuilding.inventory;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Asks questions and keeps asking until the answer is valid.
 * Every method takes the Scanner and PrintStream as parameters, so tests can feed text in and read the output
 * without a keyboard. If the input runs out, Scanner throws NoSuchElementException and the program stops.
 */
public class InputReader {

    /** Asks until the person types something other than spaces. Returns the cleaned text. */
    public static String readText(Scanner in, PrintStream out, String prompt) {
        while (true) {
            out.print(prompt);
            String text = TextUtil.clean(in.nextLine());
            if (!text.isEmpty()) {
                return text;
            }
            out.println("Please type something.");
        }
    }

    /** Asks until the person types a whole number from min to max (both allowed). */
    public static int readInt(Scanner in, PrintStream out, String prompt, int min, int max) {
        while (true) {
            out.print(prompt);
            String text = in.nextLine();
            if (!Validation.isWholeNumber(text)) {
                out.println("Please type a whole number.");
            } else {
                int number = Integer.parseInt(text.trim());
                if (number >= min && number <= max) {
                    return number;
                }
                out.println("Please type a number from " + min + " to " + max + ".");
            }
        }
    }

    /** Asks for an amount in pesos (like 125 or 125.50). Returns centavos, from 1 up to maxCentavos. */
    public static int readMoney(Scanner in, PrintStream out, String prompt, int maxCentavos) {
        while (true) {
            out.print(prompt);
            int centavos = Validation.parseCentavos(in.nextLine());
            if (centavos >= 1 && centavos <= maxCentavos) {
                return centavos;
            }
            out.println("Please type an amount like 125 or 125.50, up to " + PriceCalculator.format(maxCentavos) + ".");
        }
    }
}
