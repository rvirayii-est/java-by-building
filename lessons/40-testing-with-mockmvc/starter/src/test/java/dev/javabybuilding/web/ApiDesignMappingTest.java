package dev.javabybuilding.web;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.javabybuilding.api.ApiDesign;
import java.util.List;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    RequestMappingHandlerMapping mappings;

    @Test
    void theControllersMapExactlyTheEndpointsOfTheDesignTable() {
        // TODO 5: collect "METHOD /path" for every handler method of the controllers (mappings.getHandlerMethods(): a Map of
        // RequestMappingInfo to HandlerMethod; skip handlers outside the package dev.javabybuilding.api and the HelloController;
        // for each path of info.getPathPatternsCondition().getPatternValues() and each method of info.getMethodsCondition().getMethods()
        // add method.name() + " " + path to a sorted set). Compare with ApiDesign.ENDPOINTS mapped to Endpoint::signature, sorted.
        fail("TODO 5: write this test");
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
