package dev.javabybuilding.api;

/**
 * The JSON a client sends to record a sale. {@code product} and {@code quantity} are required; the rest is optional:
 * the customer (a walk-in when left out), the discount code ({@code none}, {@code percent:10}, {@code fixed:50.00},
 * {@code senior}) and the payment ({@code CASH}, {@code GCASH} or {@code CARD}; cash when left out).
 */
public record CreateSaleRequest(String product, int quantity, String customerName, String customerPhone, String discount, String payment) {}
