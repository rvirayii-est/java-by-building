package dev.javabybuilding.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.javabybuilding.api.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Security runs BEFORE the controllers, so the ApiExceptionHandler of lesson 39 never sees a refused request. To keep the
 * promise "every error has the same JSON shape", the security filters write the ErrorResponse themselves.
 */
final class SecurityErrors {

    private SecurityErrors() {}

    static void write(HttpServletRequest request, HttpServletResponse response, ObjectMapper json, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        json.writeValue(response.getOutputStream(), new ErrorResponse(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
    }
}
