import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What a health endpoint does, in 50 lines. JDK only. Each dependency reports UP or DOWN; the worst one decides the overall
 * status. Two different questions are asked by a platform:
 *   LIVENESS  - is the process itself broken? (then restart it)
 *   READINESS - can it take requests right now? (then send it traffic, or stop sending it traffic)
 * A database that is down makes the app NOT READY, but it is still ALIVE: restarting the app would not mend the database.
 *
 * Run:  java HealthAggregator.java
 */
public class HealthAggregator {

    static String aggregate(Map<String, String> parts) {
        for (String worst : List.of("DOWN", "OUT_OF_SERVICE", "UP")) {
            if (parts.containsValue(worst)) {
                return worst;
            }
        }
        return "UNKNOWN";
    }

    static void ask(String situation, boolean startedUp, boolean deadlocked, Map<String, String> parts) {
        boolean alive = !deadlocked;
        boolean ready = startedUp && aggregate(parts).equals("UP");
        System.out.printf("%-34s health=%-5s liveness=%-4s readiness=%-4s -> %s%n", situation, aggregate(parts),
                alive ? "UP" : "DOWN", ready ? "UP" : "DOWN",
                !alive ? "RESTART it" : ready ? "send traffic" : "keep it, but send no traffic");
    }

    public static void main(String[] args) {
        Map<String, String> allUp = new LinkedHashMap<>();
        allUp.put("db", "UP");
        allUp.put("diskSpace", "UP");
        Map<String, String> dbDown = new LinkedHashMap<>(allUp);
        dbDown.put("db", "DOWN");

        ask("normal", true, false, allUp);
        ask("still starting up", false, false, allUp);
        ask("database is down", true, false, dbDown);
        ask("the process is deadlocked", true, true, allUp);

        System.out.println();
        System.out.println("What a stranger sees:        {\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}");
        System.out.println("What an admin sees: {\"status\":\"UP\",\"components\":{\"db\":{\"status\":\"UP\"},\"diskSpace\":{\"status\":\"UP\"}}}");
    }
}
