package dev.javabybuilding.api;

import dev.javabybuilding.inventory.Sale;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * What the API tells a client about one sale (a receipt). The domain {@link Sale} holds a Customer object, a Discount
 * object and more; the client gets plain text and numbers: the customer's name, the discount's label, the payment's name.
 */
public record SaleResponse(int number, LocalDateTime soldAt, String customer, List<SaleLineResponse> lines, String discount,
                           BigDecimal subtotal, BigDecimal discountAmount, BigDecimal total, String payment) {

    public static SaleResponse from(Sale sale) {
        return new SaleResponse(sale.getNumber(), sale.getDateTime(), sale.getCustomer().getName(),
                sale.getLines().stream().map(SaleLineResponse::from).toList(), sale.getDiscount().label(),
                sale.subtotal(), sale.discountAmount(), sale.total(), sale.getPayment().name());
    }
}
