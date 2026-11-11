package dev.javabybuilding.api;

import dev.javabybuilding.inventory.ShopException;
import dev.javabybuilding.service.SaleService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** The web layer for sales. */
@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService sales;

    public SaleController(SaleService sales) {
        this.sales = sales;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> create(@RequestBody CreateSaleRequest request) {
        try {
            SaleResponse sale = sales.create(request);
            return ResponseEntity.created(URI.create("/sales/" + sale.number())).body(sale);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }

    @GetMapping
    public List<SaleResponse> list(@RequestParam(required = false) String payment) {
        try {
            return sales.list(payment);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }

    /** {@code int number}: Spring converts the text of the path to a number, and answers 400 by itself when it is not one. */
    @GetMapping("/{number}")
    public SaleResponse one(@PathVariable int number) {
        try {
            return sales.get(number);
        } catch (ShopException e) {
            throw Errors.http(e);
        }
    }
}
