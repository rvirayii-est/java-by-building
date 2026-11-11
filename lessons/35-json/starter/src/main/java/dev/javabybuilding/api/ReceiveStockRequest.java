package dev.javabybuilding.api;

/** The JSON a client sends to receive a delivery: {@code {"quantity":24}}. */
public record ReceiveStockRequest(int quantity) {}
