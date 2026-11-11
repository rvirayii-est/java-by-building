package dev.javabybuilding.api;

import java.math.BigDecimal;

/** The JSON a client sends to change a product: {@code {"price":1300.00,"reorderLevel":15}}. */
public record UpdateProductRequest(BigDecimal price, int reorderLevel) {}
