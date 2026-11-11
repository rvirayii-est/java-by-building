// Run with:  java ExceptionsDemo.java
public class ExceptionsDemo {

    public static void main(String[] args) {
        // 1. try / catch: run risky code, handle the problem.
        try {
            int cash = withdraw(500, 800);
            System.out.println("never printed " + cash);
        } catch (NotEnoughCashException e) {
            System.out.println("Caught: " + e.getMessage() + " (short by " + e.getShortBy() + ")");
        }

        // 2. finally: runs whether or not something was thrown.
        try {
            System.out.println("Opening the drawer");
            withdraw(500, 100);
        } catch (NotEnoughCashException e) {
            System.out.println("Not printed");
        } finally {
            System.out.println("Closing the drawer (finally always runs)");
        }

        // 3. built-in exceptions: parseInt throws NumberFormatException on bad text.
        for (String text : new String[] {"42", "forty-two"}) {
            try {
                System.out.println("Parsed " + Integer.parseInt(text));
            } catch (NumberFormatException e) {
                System.out.println("Not a number: " + text);
            }
        }

        // 4. one catch can handle several kinds with |
        try {
            System.out.println(10 / zero());
        } catch (ArithmeticException | NumberFormatException e) {
            System.out.println("Maths or number problem: " + e.getMessage());
        }

        // 5. a CHECKED exception (extends Exception): the compiler forces you to handle or declare it.
        try {
            loadSetting("");
        } catch (SettingMissingException e) {
            System.out.println("Setting problem: " + e.getMessage());
        }
    }

    static int zero() {
        return 0;
    }

    static int withdraw(int balance, int amount) {
        if (amount > balance) {
            throw new NotEnoughCashException(amount - balance);   // throw: stop here and jump to a matching catch
        }
        return balance - amount;
    }

    static String loadSetting(String name) throws SettingMissingException {   // "throws" warns every caller
        if (name.isEmpty()) {
            throw new SettingMissingException("no name given");
        }
        return "value";
    }
}

// Unchecked: extends RuntimeException, callers need not declare it.
class NotEnoughCashException extends RuntimeException {
    private final int shortBy;

    NotEnoughCashException(int shortBy) {
        super("Not enough cash");
        this.shortBy = shortBy;
    }

    int getShortBy() {
        return shortBy;
    }
}

// Checked: extends Exception.
class SettingMissingException extends Exception {
    SettingMissingException(String message) {
        super(message);
    }
}
