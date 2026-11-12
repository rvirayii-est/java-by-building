package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Lesson 47 drills: the small rules behind health checks, request ids, log lines and latency numbers. Complete each TODO until
 * Exercises47Test is green.
 */
public class MonitoringDrills {

    /**
     * The overall status from the status of each part ("UP", "DOWN", "OUT_OF_SERVICE", "UNKNOWN"), the way Spring Boot decides: the
     * WORST one wins, in this order of badness: DOWN, OUT_OF_SERVICE, UP, UNKNOWN. An empty map is "UNKNOWN". So one DOWN part makes the
     * whole thing DOWN, and UP beats UNKNOWN.
     */
    public static String aggregate(Map<String, String> parts) {
        for (String worst : List.of("DOWN", "OUT_OF_SERVICE", "UP")) {
            if (parts.containsValue(worst)) {
                return worst;
            }
        }
        return "UNKNOWN";
    }

    /**
     * LIVENESS: should the platform RESTART me? Only when the process itself is broken (here: deadlocked). A database that is down does
     * not make the process dead: restarting would not help.
     */
    public static boolean isAlive(boolean deadlocked) {
        return !deadlocked;
    }

    /**
     * READINESS: should the platform send me requests NOW? Only when start-up has finished and everything I depend on is UP.
     * parts is the status of each dependency, as for aggregate.
     */
    public static boolean isReady(boolean startedUp, Map<String, String> parts) {
        return startedUp && aggregate(parts).equals("UP");
    }

    private static final Pattern PLAIN = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    /** The caller's request id if it is 1 to 64 plain characters (letters, digits, '.', '_', '-'); otherwise the generator's. null counts as missing. */
    public static String safeRequestId(String incoming, Supplier<String> generator) {
        return incoming != null && PLAIN.matcher(incoming).matches() ? incoming : generator.get();
    }

    /**
     * One log line as a JSON object, keys in the order given. Values are text; escape the characters JSON requires: backslash, double
     * quote, newline (\n), carriage return (\r) and tab (\t). toJsonLine(Map.of("level", "INFO")) is {"level":"INFO"}; an empty map is {}.
     * (In the real app Spring Boot does this: structured logging. Writing it once by hand shows why you do not want to.)
     */
    public static String toJsonLine(Map<String, String> fields) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append('"').append(escape(field.getKey())).append("\":\"").append(escape(field.getValue())).append('"');
        }
        return json.append('}').toString();
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    /**
     * The p-th percentile of response times in milliseconds by the "nearest rank" rule: sort, then take the value at position
     * ceil(p / 100 * n) (counting from 1). percentile([10,20,30,40,50], 50) is 30; percentile(same, 95) is 50; percentile(same, 100) is 50.
     * An average hides slow requests; p95 says "95 of 100 requests were at least this fast". Throws IllegalArgumentException for an empty
     * list or a p outside 1..100.
     */
    public static long percentile(List<Long> millis, int p) {
        if (millis.isEmpty() || p < 1 || p > 100) {
            throw new IllegalArgumentException("Need at least one value and p between 1 and 100");
        }
        List<Long> sorted = millis.stream().sorted().toList();
        int rank = (int) Math.ceil(p / 100.0 * sorted.size());
        return sorted.get(rank - 1);
    }
}
