package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class Exercises46Test {

    @Test
    void resolveUsesTheEnvironmentThenTheDefault() {
        assertEquals("8080", ConfigDrills.resolve("${PORT:8080}", Map.of()));
        assertEquals("9000", ConfigDrills.resolve("${PORT:8080}", Map.of("PORT", "9000")));
        assertEquals("jdbc:h2:file:/data/shop", ConfigDrills.resolve("jdbc:h2:file:${DIR:/data}/shop", Map.of()));
        assertEquals("", ConfigDrills.resolve("${EMPTY:}", Map.of()));
        assertEquals("plain text", ConfigDrills.resolve("plain text", Map.of()));
    }

    @Test
    void resolveHandlesSeveralPlaceholders() {
        assertEquals("a-1-b", ConfigDrills.resolve("${X}-${Y:1}-${Z}", Map.of("X", "a", "Z", "b")));
    }

    @Test
    void aMissingSecretWithoutADefaultStopsEverything() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ConfigDrills.resolve("${SHOP_ADMIN_PASSWORD}", Map.of()));
        assertEquals("Could not resolve placeholder 'SHOP_ADMIN_PASSWORD'", e.getMessage());
    }

    @Test
    void propertyNamesBecomeEnvironmentNames() {
        assertEquals("SHOP_SECURITY_ADMIN_PASSWORD", ConfigDrills.toEnvName("shop.security.admin-password"));
        assertEquals("SERVER_PORT", ConfigDrills.toEnvName("server.port"));
    }

    @Test
    void theStrongestLayerWins() {
        Map<String, String> defaults = Map.of("server.port", "8080", "shop.seed", "true");
        Map<String, String> prodFile = Map.of("shop.seed", "false");
        Map<String, String> commandLine = Map.of("server.port", "9090");
        List<Map<String, String>> layers = List.of(defaults, prodFile, commandLine);
        assertEquals(Optional.of("9090"), ConfigDrills.effective(layers, "server.port"));
        assertEquals(Optional.of("false"), ConfigDrills.effective(layers, "shop.seed"));
        assertEquals(Optional.empty(), ConfigDrills.effective(layers, "nothing"));
    }

    @Test
    void dotEnvLinesBecomeSettings() {
        Map<String, String> env = ConfigDrills.parseDotEnv(List.of("# a comment", "", "SHOP_ADMIN_PASSWORD = \"a b=c\"", "PORT=8080", "garbage", "SHOP_X=a=b"));
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("SHOP_ADMIN_PASSWORD", "a b=c");
        expected.put("PORT", "8080");
        expected.put("SHOP_X", "a=b");
        assertEquals(expected, env);
    }

    @Test
    void redactHidesSecretsBeforeAnythingIsPrinted() {
        Map<String, String> safe = ConfigDrills.redact(new LinkedHashMap<>(Map.of("PORT", "8080", "shop.security.admin-password", "hunter2", "API_Token", "abc")));
        assertEquals("8080", safe.get("PORT"));
        assertEquals("****", safe.get("shop.security.admin-password"));
        assertEquals("****", safe.get("API_Token"));
    }
}
