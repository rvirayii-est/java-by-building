package dev.javabybuilding.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * The JSON a client sends to create a product: {@code {"name":"Salt 1kg","price":20.00,"reorderLevel":5}}.
 *
 * <p>Each rule is an annotation on the field it protects (Bean Validation). {@code reorderLevel} is an {@code Integer}, not an
 * {@code int}: an int would turn a MISSING field into 0 without a word, while an Integer stays null and {@code @NotNull} can say
 * "this is required". The rules repeat the shop's own (a price from 0.01 to 10,000.00): the shop still enforces them for every
 * caller, but here the client gets ALL the problems at once, each named by its field.
 */
public record CreateProductRequest(
        @NotBlank(message = "is required")
        @Size(max = 60, message = "must be at most 60 characters")
        String name,

        @NotNull(message = "is required")
        @DecimalMin(value = "0.01", message = "must be at least 0.01")
        @DecimalMax(value = "10000.00", message = "must be at most 10000.00")
        @Digits(integer = 5, fraction = 2, message = "must have at most 2 decimals")
        BigDecimal price,

        @NotNull(message = "is required")
        @PositiveOrZero(message = "must be 0 or more")
        Integer reorderLevel) {}
