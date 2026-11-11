package dev.javabybuilding.service;

import dev.javabybuilding.api.CreateSaleRequest;
import dev.javabybuilding.api.SaleResponse;
import dev.javabybuilding.api.UnknownSaleException;
import dev.javabybuilding.inventory.Customer;
import dev.javabybuilding.inventory.Discount;
import dev.javabybuilding.inventory.DiscountCodes;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.NoDiscount;
import dev.javabybuilding.inventory.PaymentMethod;
import dev.javabybuilding.inventory.Sale;
import dev.javabybuilding.inventory.ShopService;
import java.util.List;
import org.springframework.stereotype.Service;

/** The use cases about sales. See {@link ProductService} for what a service is (and for the locking shortcut). */
@Service
public class SaleService {

    private final ShopService shop;

    public SaleService(ShopService shop) {
        this.shop = shop;
    }

    /** Records a sale: one product, one quantity, an optional customer, discount and payment (cash when left out). */
    public SaleResponse create(CreateSaleRequest request) {
        // TODO 3: product = Checks.required(request.product(), "product"); customer = customerFrom(request); discount = a NoDiscount when
        // request.discount() is null, else DiscountCodes.decode(request.discount()); payment = PaymentMethod.CASH when
        // request.payment() is null, else paymentFrom(request.payment()). Then, inside synchronized (shop):
        // SaleResponse.from(shop.sell(product, request.quantity(), customer, discount, payment)).
        throw new UnsupportedOperationException("TODO 3");
    }

    /** All sales, oldest first, or only those paid with one method (any capitals: "gcash"). */
    public List<SaleResponse> list(String payment) {
        PaymentMethod wanted = payment == null ? null : paymentFrom(payment);
        synchronized (shop) {
            return shop.sales().stream()
                    .filter(sale -> wanted == null || sale.getPayment() == wanted)
                    .map(SaleResponse::from)
                    .toList();
        }
    }

    public SaleResponse get(int number) {
        synchronized (shop) {
            return shop.sales().stream().filter(sale -> sale.getNumber() == number).findFirst()
                    .map(SaleResponse::from)
                    .orElseThrow(() -> new UnknownSaleException(number));
        }
    }

    private static Customer customerFrom(CreateSaleRequest request) {
        String name = request.customerName();
        if (name == null) {
            return Customer.WALK_IN;
        }
        if (name.isBlank()) {
            throw new InvalidInputException("customerName must not be blank");
        }
        String phone = request.customerPhone();
        return new Customer(name.trim(), phone == null ? "" : phone.trim());
    }

    private static PaymentMethod paymentFrom(String text) {
        PaymentMethod method = PaymentMethod.fromText(text);
        if (method == null) {
            throw new InvalidInputException("payment must be Cash, GCash or Card");
        }
        return method;
    }
}
