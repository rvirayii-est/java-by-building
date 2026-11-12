package dev.javabybuilding.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The parts of the API description that no controller can say by itself: the title, how to sign in, and the error answers.
 * springdoc builds the rest (paths, parameters, request and response shapes) by reading the controllers.
 */
@Configuration
public class OpenApiConfig {

    /** The name of the sign-in method in the description. The "Authorize" button of the Swagger page uses it. */
    static final String BASIC = "basicAuth";

    @Bean
    public OpenAPI shopOpenApi() {
        // TODO 1: describe the API. Build, with io.swagger.v3.oas.models:
        //   new OpenAPI()
        //       .info(new Info().title("Inventory & Sales API").version("1.0").description("..."))
        //       .components(new Components().addSecuritySchemes(BASIC,
        //               new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic").description("HTTP Basic: username and password")))
        //       .addSecurityItem(new SecurityRequirement().addList(BASIC));
        return new OpenAPI();
    }

    /** Adds the shared error shape to the description, and the 401 and 403 answers to every operation that needs a sign-in. */
    @Bean
    public OpenApiCustomizer errorResponses() {
        // TODO 2: (stretch of the lesson) add the ErrorResponse schema to openApi.getComponents() with ModelConverters.getInstance().readAll(ErrorResponse.class),
        // and for every operation except /hello add the responses "401" and "403" that point to "#/components/schemas/ErrorResponse".
        // The solution of the lesson shows one way. OpenApiTest.theErrorShapeIsDescribed... checks it.
        return openApi -> { };
    }
}
