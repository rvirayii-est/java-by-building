import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * What a CI server does, in 50 lines. JDK only. A pipeline is a list of steps run on a clean machine. It is FAIL-FAST: the first
 * failing step stops the run and the run is marked red. A cache turns a slow first run into a fast second one.
 *
 * Run:  java PipelineDemo.java
 */
public class PipelineDemo {

    record Step(String name, BooleanSupplier action) {}

    static boolean run(String title, List<Step> steps) {
        System.out.println("== " + title);
        for (Step step : steps) {
            boolean ok = step.action().getAsBoolean();
            System.out.println("  " + (ok ? "ok    " : "FAILED") + " " + step.name());
            if (!ok) {
                System.out.println("  (the remaining steps are skipped)");
                System.out.println("  result: RED");
                return false;
            }
        }
        System.out.println("  result: GREEN");
        return true;
    }

    static boolean libraryCacheFilled = false; // stands for ~/.m2, kept between runs by the cache

    public static void main(String[] args) {
        Step checkout = new Step("checkout the code", () -> true);
        Step java = new Step("install Java 21", () -> true);
        Step build = new Step("./mvnw -B verify (download libraries, compile, test)", () -> {
            System.out.println("         " + (libraryCacheFilled ? "libraries restored from the cache: fast" : "no cache yet: downloading every library: slow"));
            libraryCacheFilled = true; // the cache is saved at the end of a green run
            return true;
        });

        run("run 1: first push", List.of(checkout, java, build));
        run("run 2: second push, same pom.xml", List.of(checkout, java, build));

        Step brokenTest = new Step("./mvnw -B verify (one test fails)", () -> false);
        Step deploy = new Step("publish the result", () -> true);
        run("run 3: a pull request with a bug", List.of(checkout, java, brokenTest, deploy));
    }
}
