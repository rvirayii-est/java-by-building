package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercises36Test {

    static class ProductController {}

    static class URLShortener {}

    static class X {}

    static class ShopService {}

    @Test
    void beanNameLowersTheFirstLetter() {
        assertEquals("productController", SpringDrills.beanName(ProductController.class));
        assertEquals("x", SpringDrills.beanName(X.class));
        assertEquals("shopService", SpringDrills.beanName(ShopService.class));
    }

    @Test
    void beanNameKeepsAcronymsAsTheyAre() {
        assertEquals("URLShortener", SpringDrills.beanName(URLShortener.class));
    }

    @Test
    void resolveFillsInValuesAndDefaults() {
        assertEquals("http://localhost:8080/", SpringDrills.resolve("http://${host:localhost}:${port}/", Map.of("port", "8080")));
        assertEquals("http://shop.local:9090/", SpringDrills.resolve("http://${host:localhost}:${port}/", Map.of("host", "shop.local", "port", "9090")));
    }

    @Test
    void resolveLeavesPlainTextAndAllowsAnEmptyDefault() {
        assertEquals("no placeholders", SpringDrills.resolve("no placeholders", Map.of()));
        assertEquals("[]", SpringDrills.resolve("[${x:}]", Map.of()));
        assertEquals("a:b", SpringDrills.resolve("${a}", Map.of("a", "a:b")));
    }

    @Test
    void resolveRefusesAKeyWithNothingBehindIt() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SpringDrills.resolve("port=${port}", Map.of()));
        assertEquals("Could not resolve placeholder 'port'", e.getMessage());
    }

    @Test
    void matchPathFindsTheVariables() {
        assertEquals(Map.of("name", "Rice"), SpringDrills.matchPath("/products/{name}", "/products/Rice"));
        assertEquals(Map.of("name", "Rice", "x", "7"), SpringDrills.matchPath("/products/{name}/stock/{x}", "/products/Rice/stock/7"));
        assertEquals(Map.of(), SpringDrills.matchPath("/products", "/products"));
    }

    @Test
    void matchPathReturnsNullWhenThePathDoesNotFit() {
        assertNull(SpringDrills.matchPath("/products/{name}", "/sales/7"));
        assertNull(SpringDrills.matchPath("/products/{name}", "/products"));
        assertNull(SpringDrills.matchPath("/products", "/products/Rice"));
    }

    @Test
    void theMostSpecificSettingWins() {
        Map<String, String> cmd = Map.of("server.port", "9000");
        Map<String, String> env = Map.of("server.port", "8500", "app.name", "from-env");
        Map<String, String> file = Map.of("server.port", "8080", "app.name", "from-file", "app.mode", "file");
        assertEquals("9000", SpringDrills.chooseSetting("server.port", cmd, env, file, "1"));
        assertEquals("from-env", SpringDrills.chooseSetting("app.name", cmd, env, file, "default"));
        assertEquals("file", SpringDrills.chooseSetting("app.mode", cmd, env, file, "default"));
        assertEquals("default", SpringDrills.chooseSetting("other", cmd, env, file, "default"));
    }

    @Test
    void profileFilesComeAfterTheMainFile() {
        assertEquals(List.of("application.properties"), SpringDrills.configFiles(List.of()));
        assertEquals(List.of("application.properties", "application-dev.properties", "application-h2.properties"),
                SpringDrills.configFiles(List.of("dev", "h2")));
    }
}
