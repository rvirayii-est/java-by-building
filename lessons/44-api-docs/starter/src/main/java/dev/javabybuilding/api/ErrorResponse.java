package dev.javabybuilding.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Every error the API sends has this ONE shape, so a client writes one piece of code to read all of them:
 * the status number, its standard words, a message a person can read, the path that was asked for, and, for a request with bad
 * fields, one entry per problem. {@code problems} is left out of the JSON when it is empty.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(int status, String error, String message, String path, List<FieldProblem> problems) {

    /** An error with no per-field details. */
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, List.of());
    }

    /** One bad field: its name (for example "price") and what is wrong with it ("is required"). */
    public record FieldProblem(String field, String message) {}
}
