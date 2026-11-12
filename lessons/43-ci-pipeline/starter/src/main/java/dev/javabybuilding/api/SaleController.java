package dev.javabybuilding.api;

import dev.javabybuilding.service.SaleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody CreateSaleRequest request) {
        SaleResponse sale = sales.create(request);
        return ResponseEntity.created(URI.create("/sales/" + sale.number())).body(sale);
    }

    @GetMapping
    public List<SaleResponse> list(@RequestParam(required = false)
                                   @Pattern(regexp = "(?i)cash|gcash|card", message = "must be Cash, GCash or Card") String payment) {
        return sales.list(payment);
    }

    /** A sale number is a whole number of 1 or more: {@code /sales/abc} and {@code /sales/0} are both 400, not 404. */
    @GetMapping("/{number}")
    public SaleResponse one(@PathVariable @Min(value = 1, message = "must be at least 1") int number) {
        return sales.get(number);
    }
}
