import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Why the ORDER of a Dockerfile matters. JDK only. Docker keeps each step (layer) and reuses it while the step and everything
 * before it are unchanged. The first changed step, and all steps after it, run again. This program replays that rule for two
 * Dockerfile layouts after you edit one Java file.
 *
 * Run:  java LayerCacheDemo.java
 */
public class LayerCacheDemo {

    record Step(String instruction, List<String> inputFiles, int seconds) {}

    /** Runs the steps; a step is "cached" when it and all earlier steps used the same file versions as in the previous build. */
    static int build(String title, List<Step> steps, Map<String, Integer> files, Map<String, Integer> previous) {
        System.out.println("== " + title);
        int total = 0;
        boolean broken = false; // once one layer changes, every later layer must run again
        for (Step step : steps) {
            for (String file : step.inputFiles()) {
                if (previous == null || !files.get(file).equals(previous.get(file))) {
                    broken = true;
                }
            }
            int cost = broken ? step.seconds() : 0;
            total += cost;
            System.out.printf("  %-44s %s%n", step.instruction(), broken ? "RUN    (" + cost + " s)" : "cached (0 s)");
        }
        System.out.println("  total: " + total + " s");
        return total;
    }

    public static void main(String[] args) {
        Map<String, Integer> v1 = new LinkedHashMap<>(Map.of("pom.xml", 1, "src/Shop.java", 1));
        Map<String, Integer> v2 = new LinkedHashMap<>(Map.of("pom.xml", 1, "src/Shop.java", 2)); // you edited one Java file

        List<Step> naive = List.of(
                new Step("COPY . .", List.of("pom.xml", "src/Shop.java"), 1),
                new Step("RUN mvn package (downloads libraries)", List.of(), 60));
        List<Step> cacheFriendly = List.of(
                new Step("COPY pom.xml .", List.of("pom.xml"), 1),
                new Step("RUN mvn dependency:go-offline", List.of(), 60),
                new Step("COPY src ./src", List.of("src/Shop.java"), 1),
                new Step("RUN mvn package -DskipTests", List.of(), 8));

        build("naive Dockerfile, first build", naive, v1, null);
        build("naive Dockerfile, after editing one Java file", naive, v2, v1);
        System.out.println();
        build("cache-friendly Dockerfile, first build", cacheFriendly, v1, null);
        build("cache-friendly Dockerfile, after editing one Java file", cacheFriendly, v2, v1);
        System.out.println();
        System.out.println("Rule: put what changes RARELY (the list of libraries) before what changes OFTEN (your code).");
    }
}
