// Run with:  java MenuDemo.java
import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class MenuDemo {
    public static void main(String[] args) {
        // A pretend keyboard, so this example never waits. Real programs use new Scanner(System.in).
        String pretendTyping = "1\n1\nx\n2\n7\n3\n";
        Scanner in = new Scanner(new ByteArrayInputStream(pretendTyping.getBytes()));

        int counter = 0;
        boolean running = true;
        while (running) {                       // the menu loop: show, ask, act, repeat
            System.out.println();
            System.out.println("1. Add one");
            System.out.println("2. Show counter");
            System.out.println("3. Quit");
            System.out.print("Choose 1-3: ");

            String line = in.nextLine();
            System.out.println(line);

            if (line.equals("1")) {
                counter++;
                System.out.println("Added. Counter is now " + counter);
            } else if (line.equals("2")) {
                System.out.println("Counter is " + counter);
            } else if (line.equals("3")) {
                System.out.println("Goodbye.");
                running = false;                 // the loop ends the next time it checks
            } else {
                System.out.println("Please choose 1, 2 or 3.");
            }
        }
    }
}
