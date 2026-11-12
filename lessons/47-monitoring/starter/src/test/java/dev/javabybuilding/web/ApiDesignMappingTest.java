package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.javabybuilding.api.ApiDesign;
import java.util.List;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Lesson 34's design table against the real application: ask Spring MVC which (method, path) pairs it has mapped, and compare them
 * with {@link ApiDesign#ENDPOINTS}. Add an endpoint to the controllers and forget the table (or the other way round) and this fails.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mvc-it;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class ApiDesignMappingTest {

    @Autowired
    @Qualifier("requestMappingHandlerMapping") // the actuator adds a second mapping (controllerEndpointHandlerMapping) of its own
    RequestMappingHandlerMapping mappings;

    @Test
    void theControllersMapExactlyTheEndpointsOfTheDesignTable() {
        TreeSet<String> mapped = new TreeSet<>();
        for (var entry : mappings.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handler = entry.getValue();
            if (!handler.getBeanType().getPackageName().equals("dev.javabybuilding.api") || handler.getBeanType().getSimpleName().equals("HelloController")) {
                continue; // Spring's own /error page, and the hello example of lesson 36
            }
            for (String path : info.getPathPatternsCondition().getPatternValues()) {
                info.getMethodsCondition().getMethods().forEach(method -> mapped.add(method.name() + " " + path));
            }
        }
        List<String> design = ApiDesign.ENDPOINTS.stream().map(ApiDesign.Endpoint::signature).sorted().toList();
        assertEquals(design, List.copyOf(mapped));
    }

    @Test
    void everyControllerMethodDeclaresAnHttpMethod() {
        mappings.getHandlerMethods().forEach((info, handler) -> {
            if (handler.getBeanType().getPackageName().equals("dev.javabybuilding.api")) {
                assertEquals(false, info.getMethodsCondition().getMethods().isEmpty(), handler + " must say GET, POST, PUT or DELETE");
            }
        });
    }

    @Test
    void theClassLevelPathsOfTheControllersAreTheThreeResources() {
        TreeSet<String> prefixes = new TreeSet<>();
        mappings.getHandlerMethods().values().forEach(handler -> {
            RequestMapping onClass = handler.getBeanType().getAnnotation(RequestMapping.class);
            if (onClass != null && handler.getBeanType().getPackageName().equals("dev.javabybuilding.api")) {
                prefixes.addAll(List.of(onClass.value()));
            }
        });
        assertEquals(List.of("/products", "/reports", "/sales"), List.copyOf(prefixes));
    }
}
