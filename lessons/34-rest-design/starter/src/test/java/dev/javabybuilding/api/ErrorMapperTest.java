package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.ProductInUseException;
import dev.javabybuilding.inventory.ShopException;
import dev.javabybuilding.inventory.UnknownProductException;
import org.junit.jupiter.api.Test;

class ErrorMapperTest {

    @Test
    void notFoundProblemsAre404() {
        assertEquals(404, ErrorMapper.statusFor(new UnknownProductException("salt")));
        assertEquals(404, ErrorMapper.statusFor(new UnknownSaleException(9)));
    }

    @Test
    void clashesWithTheCurrentStateAre409() {
        assertEquals(409, ErrorMapper.statusFor(new InsufficientStockException("Rice 25kg", 13, 12)));
        assertEquals(409, ErrorMapper.statusFor(new ProductAlreadyExistsException("Rice 25kg")));
        assertEquals(409, ErrorMapper.statusFor(new ProductInUseException("Rice 25kg")));
    }

    @Test
    void badInputIs400EvenForAnyOtherShopException() {
        assertEquals(400, ErrorMapper.statusFor(new InvalidInputException("Quantity must be at least 1.")));
        assertEquals(400, ErrorMapper.statusFor(new ShopException("something else about the shop")));
    }

    @Test
    void anythingElseIsABugAnd500() {
        assertEquals(500, ErrorMapper.statusFor(new IllegalStateException("oops")));
        assertEquals(500, ErrorMapper.statusFor(new NullPointerException()));
    }
}
