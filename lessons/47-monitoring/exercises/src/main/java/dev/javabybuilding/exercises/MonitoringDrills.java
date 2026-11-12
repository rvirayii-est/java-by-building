package dev.javabybuilding.exercises;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
        // TODO 1: for each of "DOWN", "OUT_OF_SERVICE", "UP" in that order: if parts.containsValue(it) return it. Otherwise "UNKNOWN".
        return null;
    }

    /**
     * LIVENESS: should the platform RESTART me? Only when the process itself is broken (here: deadlocked). A database that is down does
     * not make the process dead: restarting would not help.
     */
    public static boolean isAlive(boolean deadlocked) {
        // TODO 2
        return false;
    }

    /**
     * READINESS: should the platform send me requests NOW? Only when start-up has finished and everything I depend on is UP.
     * parts is the status of each dependency, as for aggregate.
     */
    public static boolean isReady(boolean startedUp, Map<String, String> parts) {
        // TODO 3: startedUp AND aggregate(parts) is "UP".
        return false;
    }

    /** The caller's request id if it is 1 to 64 plain characters (letters, digits, '.', '_', '-'); otherwise the generator's. null counts as missing. */
    public static String safeRequestId(String incoming, Supplier<String> generator) {
        // TODO 4: Pattern.compile("[A-Za-z0-9._-]{1,64}") and matcher(incoming).matches(); generator.get() otherwise.
        return null;
    }

    /**
     * One log line as a JSON object, keys in the order given. Values are text; escape the characters JSON requires: backslash, double
     * quote, newline (\n), carriage return (\r) and tab (\t). toJsonLine(Map.of("level", "INFO")) is {"level":"INFO"}; an empty map is {}.
     * (In the real app Spring Boot does this: structured logging. Writing it once by hand shows why you do not want to.)
     */
    public static String toJsonLine(Map<String, String> fields) {
        // TODO 5: StringBuilder, a comma between fields, and a helper escape(text) that replaces the backslash FIRST, then the quote, \n, \r, \t.
        return null;
    }

    /**
     * The p-th percentile of response times in milliseconds by the "nearest rank" rule: sort, then take the value at position
     * ceil(p / 100 * n) (counting from 1). percentile([10,20,30,40,50], 50) is 30; percentile(same, 95) is 50; percentile(same, 100) is 50.
     * An average hides slow requests; p95 says "95 of 100 requests were at least this fast". Throws IllegalArgumentException for an empty
     * list or a p outside 1..100.
     */
    public static long percentile(List<Long> millis, int p) {
        // TODO 6: check the arguments, sort (millis.stream().sorted().toList()), rank = (int) Math.ceil(p / 100.0 * size), get(rank - 1).
        return 0;
    }
}
