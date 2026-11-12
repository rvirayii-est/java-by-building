package dev.javabybuilding.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The JSON a client sends to record a sale. {@code product} and {@code quantity} are required; the rest is optional:
 * the customer (a walk-in when left out), the discount code and the payment (cash when left out).
 *
 * <p>An optional field has NO {@code @NotNull}. Most constraints treat null as "nothing to check" (only {@code @NotNull},
 * {@code @NotBlank} and {@code @NotEmpty} complain about it), so {@code @Pattern} below only looks at fields that were sent.
 */
public record CreateSaleRequest(
        @NotBlank(message = "is required")
        String product,

        @NotNull(message = "is required")
        @Min(value = 1, message = "must be at least 1")
        Integer quantity,

        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        @Size(max = 60, message = "must be at most 60 characters")
        String customerName,

        @Size(max = 20, message = "must be at most 20 characters")
        @Pattern(regexp = "[0-9+() -]*", message = "may contain only digits, spaces and + ( ) -")
        String customerPhone,

        @Pattern(regexp = "none|senior|percent:\\d{1,3}|fixed:\\d+(\\.\\d{1,2})?",
                message = "must be none, senior, percent:<number> or fixed:<amount>")
        String discount,

        @Pattern(regexp = "(?i)cash|gcash|card", message = "must be Cash, GCash or Card")
        String payment) {}
