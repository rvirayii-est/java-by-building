package dev.javabybuilding.api;

import java.math.BigDecimal;

/** The JSON a client sends to create a product: {@code {"name":"Salt 1kg","price":20.00,"reorderLevel":5}}. */
public record CreateProductRequest(String name, BigDecimal price, int reorderLevel) {}
