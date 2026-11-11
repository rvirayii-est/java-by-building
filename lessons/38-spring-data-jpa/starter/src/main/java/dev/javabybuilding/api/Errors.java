package dev.javabybuilding.api;

import dev.javabybuilding.inventory.ShopException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Turns a problem the shop reported (a ShopException) into the HTTP answer Spring understands. {@link ErrorMapper} decides the
 * number; this class wraps it. Every controller method catches ShopException and calls this: the same try/catch again and
 * again. Lesson 39 removes all of them with ONE class for the whole app (@RestControllerAdvice).
 */
final class Errors {

    private Errors() {}

    static ResponseStatusException http(ShopException problem) {
        return new ResponseStatusException(HttpStatus.valueOf(ErrorMapper.statusFor(problem)), problem.getMessage());
    }
}
