// Run with:  java ScannerDemo.java
import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class ScannerDemo {
    public static void main(String[] args) {
        // For the real keyboard you would write:  Scanner in = new Scanner(System.in);
        // Here we feed the Scanner a fixed text instead, so this example never waits for you.
        String pretendTyping = "Rice 25kg\nabc\n3\n125.50\n";
        Scanner in = new Scanner(new ByteArrayInputStream(pretendTyping.getBytes()));

        System.out.print("Product name: ");
        String name = in.nextLine();           // reads one whole line (without the Enter)
        System.out.println(name);

        int quantity = -1;
        while (quantity < 1) {                  // keep asking until the answer is valid
            System.out.print("Quantity: ");
            String line = in.nextLine();
            System.out.println(line);
            boolean allDigits = !line.isEmpty();
            for (int i = 0; i < line.length(); i++) {
                if (!Character.isDigit(line.charAt(i))) {
                    allDigits = false;
                }
            }
            if (allDigits) {
                quantity = Integer.parseInt(line);
            } else {
                System.out.println("Please type a whole number.");
            }
        }

        System.out.print("Price (PHP): ");
        String priceText = in.nextLine();
        System.out.println(priceText);
        int dot = priceText.indexOf('.');
        int pesos = Integer.parseInt(priceText.substring(0, dot));
        int centavos = Integer.parseInt(priceText.substring(dot + 1));
        int unitPrice = pesos * 100 + centavos; // money stays in whole centavos

        System.out.println(name + " x " + quantity + " = " + (unitPrice * quantity) + " centavos");

        // hasNextLine() answers: is there more input? (At the end of input, nextLine() would crash.)
        System.out.println("More input? " + in.hasNextLine());
        in.close();
    }
}
