package dev.javabybuilding.config;

import dev.javabybuilding.api.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
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
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory & Sales API")
                        .version("1.0")
                        .description("""
                                The REST API of a small shop (the Java by Building course project).
                                Sign in with HTTP Basic. Two demo users exist on a fresh install: admin and cashier.
                                Every error has the same JSON shape: status, error, message, path and, for bad fields, problems.""")
                        .contact(new Contact().name("Java by Building").url("https://github.com/rvirayii-est/java-by-building")))
                .components(new Components().addSecuritySchemes(BASIC,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic").description("HTTP Basic: username and password")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC)); // every operation needs it, unless it says otherwise
    }

    /** Adds the shared error shape to the description, and the 401 and 403 answers to every operation that needs a sign-in. */
    @Bean
    public OpenApiCustomizer errorResponses() {
        return openApi -> {
            ModelConverters.getInstance().readAll(ErrorResponse.class).forEach(openApi.getComponents()::addSchemas);
            ModelConverters.getInstance().readAll(ErrorResponse.FieldProblem.class).forEach(openApi.getComponents()::addSchemas);
            Content errorBody = new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")));
            openApi.getPaths().forEach((path, item) -> item.readOperations().forEach(operation -> {
                if (path.equals("/hello")) {
                    operation.setSecurity(List.of()); // the one public endpoint
                    return;
                }
                operation.getResponses().addApiResponse("401", new ApiResponse().description("Not signed in, or a wrong password").content(errorBody));
                operation.getResponses().addApiResponse("403", new ApiResponse().description("Signed in, but the role is not allowed to do this").content(errorBody));
            }));
        };
    }
}
