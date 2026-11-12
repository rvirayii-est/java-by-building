import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Two small checks every portfolio deserves, JDK only: does the 5-minute demo really fit in 5 minutes, and is each resume bullet
 * specific (starts with a verb, has a number)? Run them on your own demo plan and bullets before anyone else reads them.
 *
 * Run:  java DemoTimer.java
 */
public class DemoTimer {

    record Part(String name, int seconds) {}

    static String label(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    static final Set<String> VERBS = Set.of("built", "designed", "added", "wrote", "secured", "documented", "containerised", "automated", "tested");

    static String check(String bullet) {
        String first = bullet.split("\\s+")[0].toLowerCase(Locale.ROOT);
        boolean verb = VERBS.contains(first);
        boolean number = bullet.matches(".*\\d.*");
        return verb && number ? "good" : "weak:" + (verb ? "" : " no action verb;") + (number ? "" : " no number;");
    }

    public static void main(String[] args) {
        List<Part> plan = List.of(new Part("what it is", 30), new Part("secured", 45), new Part("roles", 60), new Part("my feature", 60),
                new Part("documentation", 30), new Part("operated", 30), new Part("proof", 30), new Part("close", 15));
        int clock = 0;
        for (Part part : plan) {
            System.out.printf("%s - %s  %s%n", label(clock), label(clock + part.seconds()), part.name());
            clock += part.seconds();
        }
        System.out.println("total " + label(clock) + (clock == 300 ? "  fits in 5 minutes" : "  DOES NOT FIT: cut " + label(Math.abs(clock - 300))));

        System.out.println();
        for (String bullet : List.of(
                "Built a secured REST API with 12 endpoints and 321 automated tests",
                "Responsible for the backend of a shop system",
                "Secured the API with BCrypt and two roles")) {
            System.out.println(check(bullet) + "  <-  " + bullet);
        }
    }
}
