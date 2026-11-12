package dev.javabybuilding.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** The JSON a client sends to change a product: {@code {"price":1300.00,"reorderLevel":15}}. */
public record UpdateProductRequest(
        @NotNull(message = "is required")
        @DecimalMin(value = "0.01", message = "must be at least 0.01")
        @DecimalMax(value = "10000.00", message = "must be at most 10000.00")
        @Digits(integer = 5, fraction = 2, message = "must have at most 2 decimals")
        BigDecimal price,

        @NotNull(message = "is required")
        @PositiveOrZero(message = "must be 0 or more")
        Integer reorderLevel) {}
