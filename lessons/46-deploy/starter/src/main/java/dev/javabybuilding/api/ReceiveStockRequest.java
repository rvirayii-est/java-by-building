package dev.javabybuilding.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** The JSON a client sends to receive a delivery: {@code {"quantity":24}}. */
public record ReceiveStockRequest(
        @NotNull(message = "is required")
        @Min(value = 1, message = "must be at least 1")
        Integer quantity) {}
