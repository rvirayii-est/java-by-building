package dev.javabybuilding.api;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import dev.javabybuilding.api.ErrorResponse.FieldProblem;
import dev.javabybuilding.inventory.ShopException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * ONE place that turns every kind of problem into the same {@link ErrorResponse}. {@code @RestControllerAdvice} means: "advice
 * for every controller; what my methods return is written as the response". Each {@code @ExceptionHandler} method says which
 * exception it takes care of. When a controller (or Spring itself, before the controller) throws, Spring looks for the most specific
 * handler here.
 *
 * <p>Three rules this class follows:
 * <ol>
 *   <li><b>One shape.</b> Whatever went wrong, the client reads status, error, message, path (and problems for bad fields).</li>
 *   <li><b>The right number.</b> The shop's exceptions become statuses through {@link ErrorMapper}, the table of lesson 34.</li>
 *   <li><b>Nothing secret.</b> A bug (an unexpected exception) is logged here, with its stack trace, and the client only sees
 *       "Something went wrong on the server." No class names, no SQL, no file paths.</li>
 * </ol>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // ---- the shop's own problems: 404, 400, 409 (and 500 for a database fault) ----

    @ExceptionHandler(ShopException.class)
    public ResponseEntity<ErrorResponse> shopProblem(ShopException e, HttpServletRequest request) {
        int status = ErrorMapper.statusFor(e);
        if (status >= 500) {
            LOG.error("The shop failed on {}", request.getRequestURI(), e);
            return build(status, "Something went wrong on the server.", request, List.of());
        }
        return build(status, e.getMessage(), request, List.of());
    }

    // ---- bad requests: 400 with the fields that are wrong ----

    /** {@code @Valid} on a request body failed: one problem per bad field, sorted so the answer is always in the same order. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidBody(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldProblem> problems = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldProblem(error.getField(), error.getDefaultMessage()))
                .sorted(PROBLEM_ORDER)
                .toList();
        return build(400, "The request has invalid fields.", request, problems);
    }

    /** A constraint on a parameter failed ({@code @Pattern} on ?status=, {@code @Min} on the sale number). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> invalidParameter(HandlerMethodValidationException e, HttpServletRequest request) {
        List<FieldProblem> problems = e.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldProblem(String.valueOf(result.getMethodParameter().getParameterName()), error.getDefaultMessage())))
                .sorted(PROBLEM_ORDER)
                .toList();
        return build(400, "The request has invalid parameters.", request, problems);
    }

    /** The JSON could not be read: broken text, an unknown field, a value of the wrong kind, or no body at all. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> unreadableBody(HttpMessageNotReadableException e, HttpServletRequest request) {
        Throwable cause = e.getCause();
        if (cause instanceof UnrecognizedPropertyException unknown) {
            return build(400, "The request has a field that does not exist.", request,
                    List.of(new FieldProblem(unknown.getPropertyName(), "is not a known field")));
        }
        if (cause instanceof JsonMappingException mapping && !mapping.getPath().isEmpty()) {
            String field = mapping.getPath().stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));
            return build(400, "The request has a value of the wrong kind.", request, List.of(new FieldProblem(field, "has the wrong kind of value")));
        }
        if (e.getMessage() != null && e.getMessage().startsWith("Required request body is missing")) {
            return build(400, "The request body is required.", request, List.of());
        }
        return build(400, "The request body is not valid JSON.", request, List.of()); // no details: they would show our own classes
    }

    /** A path part or parameter that should be a number is not ({@code /sales/abc}). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> wrongType(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        boolean number = e.getRequiredType() != null && (Number.class.isAssignableFrom(e.getRequiredType()) || e.getRequiredType().isPrimitive());
        return build(400, "The request has a value of the wrong kind.", request,
                List.of(new FieldProblem(e.getName(), number ? "must be a whole number" : "has the wrong kind of value")));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingParameter(MissingServletRequestParameterException e, HttpServletRequest request) {
        return build(400, "The request is missing a parameter.", request, List.of(new FieldProblem(e.getParameterName(), "is required")));
    }

    // ---- the request does not fit what the API offers: 404, 405, 415 ----

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noSuchPath(HttpServletRequest request) {
        return build(404, "No such path: " + request.getRequestURI(), request, List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> wrongMethod(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        List<String> allowed = e.getSupportedHttpMethods() == null ? List.of()
                : e.getSupportedHttpMethods().stream().map(HttpMethod::name).sorted().toList();
        ResponseEntity<ErrorResponse> response = build(405, e.getMethod() + " is not allowed here. Use " + String.join(", ", allowed) + ".", request, List.of());
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        headers.set(HttpHeaders.ALLOW, String.join(", ", allowed));
        return new ResponseEntity<>(response.getBody(), headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> wrongContentType(HttpServletRequest request) {
        return build(415, "The Content-Type must be application/json.", request, List.of());
    }

    // ---- the server's own problems: 409 for a clash in the database, 500 for everything else ----

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> clash(DataIntegrityViolationException e, HttpServletRequest request) {
        LOG.warn("The database refused a change on {}: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());
        return build(409, "The request clashes with data that already exists.", request, List.of());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> databaseFault(DataAccessException e, HttpServletRequest request) {
        LOG.error("Database problem on {}", request.getRequestURI(), e);
        return build(500, "Something went wrong on the server.", request, List.of());
    }

    /** The safety net: anything nobody expected. The details go to the log; the client gets a polite, empty answer. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> bug(Exception e, HttpServletRequest request) {
        LOG.error("Unexpected failure on {}", request.getRequestURI(), e);
        return build(500, "Something went wrong on the server.", request, List.of());
    }

    // ---- helpers ----

    private static final Comparator<FieldProblem> PROBLEM_ORDER =
            Comparator.comparing(FieldProblem::field).thenComparing(FieldProblem::message);

    private static ResponseEntity<ErrorResponse> build(int status, String message, HttpServletRequest request, List<FieldProblem> problems) {
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(status, httpStatus.getReasonPhrase(), message, request.getRequestURI(), problems));
    }
}
