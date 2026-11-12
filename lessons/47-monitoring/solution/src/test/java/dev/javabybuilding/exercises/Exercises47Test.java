package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercises47Test {

    @Test
    void theWorstPartDecidesTheOverallStatus() {
        assertEquals("UP", MonitoringDrills.aggregate(Map.of("db", "UP", "disk", "UP")));
        assertEquals("DOWN", MonitoringDrills.aggregate(Map.of("db", "DOWN", "disk", "UP")));
        assertEquals("OUT_OF_SERVICE", MonitoringDrills.aggregate(Map.of("db", "OUT_OF_SERVICE", "disk", "UP")));
        assertEquals("DOWN", MonitoringDrills.aggregate(Map.of("a", "OUT_OF_SERVICE", "b", "DOWN")));
        assertEquals("UP", MonitoringDrills.aggregate(Map.of("a", "UNKNOWN", "b", "UP")));
        assertEquals("UNKNOWN", MonitoringDrills.aggregate(Map.of()));
    }

    @Test
    void livenessAndReadinessAreDifferentQuestions() {
        assertTrue(MonitoringDrills.isAlive(false));
        assertFalse(MonitoringDrills.isAlive(true));
        Map<String, String> dbDown = Map.of("db", "DOWN");
        assertTrue(MonitoringDrills.isAlive(false), "a database that is down does not make the process dead");
        assertFalse(MonitoringDrills.isReady(true, dbDown), "but it is not ready for requests");
        assertFalse(MonitoringDrills.isReady(false, Map.of("db", "UP")), "still starting up");
        assertTrue(MonitoringDrills.isReady(true, Map.of("db", "UP")));
    }

    @Test
    void safeRequestIdKeepsPlainIdsAndReplacesTheRest() {
        assertEquals("order-4711", MonitoringDrills.safeRequestId("order-4711", () -> "generated"));
        assertEquals("generated", MonitoringDrills.safeRequestId(null, () -> "generated"));
        assertEquals("generated", MonitoringDrills.safeRequestId("a\nb", () -> "generated"));
        assertEquals("generated", MonitoringDrills.safeRequestId("x".repeat(65), () -> "generated"));
        assertEquals("x".repeat(64), MonitoringDrills.safeRequestId("x".repeat(64), () -> "generated"));
    }

    @Test
    void aLogLineIsOneJsonObject() {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("level", "INFO");
        fields.put("message", "GET /products -> 200");
        assertEquals("{\"level\":\"INFO\",\"message\":\"GET /products -> 200\"}", MonitoringDrills.toJsonLine(fields));
        assertEquals("{}", MonitoringDrills.toJsonLine(Map.of()));
    }

    @Test
    void jsonEscapingKeepsALogLineOnOneLineAndStopsForgery() {
        String line = MonitoringDrills.toJsonLine(Map.of("message", "he said \"hi\"\nINFO fake\t\\"));
        assertEquals("{\"message\":\"he said \\\"hi\\\"\\nINFO fake\\t\\\\\"}", line);
        assertFalse(line.contains("\n"));
    }

    @Test
    void percentilesUseTheNearestRank() {
        List<Long> times = List.of(50L, 10L, 40L, 20L, 30L);
        assertEquals(30, MonitoringDrills.percentile(times, 50));
        assertEquals(50, MonitoringDrills.percentile(times, 95));
        assertEquals(10, MonitoringDrills.percentile(times, 1));
        assertEquals(50, MonitoringDrills.percentile(times, 100));
    }

    @Test
    void theAverageHidesTheSlowRequestButP95DoesNot() {
        List<Long> times = new java.util.ArrayList<>(java.util.Collections.nCopies(95, 10L));
        times.addAll(java.util.Collections.nCopies(5, 2000L));
        assertEquals(10, MonitoringDrills.percentile(times, 95));
        assertEquals(2000, MonitoringDrills.percentile(times, 96));
        assertEquals(109.5, times.stream().mapToLong(Long::longValue).average().orElseThrow(), 0.001);
    }

    @Test
    void percentileRefusesNonsense() {
        assertThrows(IllegalArgumentException.class, () -> MonitoringDrills.percentile(List.of(), 50));
        assertThrows(IllegalArgumentException.class, () -> MonitoringDrills.percentile(List.of(1L), 0));
        assertThrows(IllegalArgumentException.class, () -> MonitoringDrills.percentile(List.of(1L), 101));
    }
}
