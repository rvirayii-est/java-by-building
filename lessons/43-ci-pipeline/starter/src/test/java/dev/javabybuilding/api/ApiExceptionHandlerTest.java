package dev.javabybuilding.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.javabybuilding.inventory.DatabaseException;
import dev.javabybuilding.inventory.InsufficientStockException;
import dev.javabybuilding.inventory.InvalidInputException;
import dev.javabybuilding.inventory.ProductInUseException;
import dev.javabybuilding.inventory.UnknownProductException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * The error handler is an ordinary class: give it an exception and a fake request (Spring ships {@code MockHttpServletRequest} for
 * that) and look at the answer. No server, no context.
 */
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/products/salt");

    @Test
    void anUnknownProductIsA404WithTheShopsMessage() {
        ResponseEntity<ErrorResponse> response = handler.shopProblem(new UnknownProductException("salt"), request);
        assertEquals(404, response.getStatusCode().value());
        assertEquals(new ErrorResponse(404, "Not Found", "Unknown product: salt", "/products/salt"), response.getBody());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    void conflictsAre409AndBadInputIs400() {
        assertEquals(409, handler.shopProblem(new InsufficientStockException("Rice 25kg", 13, 12), request).getStatusCode().value());
        assertEquals(409, handler.shopProblem(new ProductAlreadyExistsException("Rice 25kg"), request).getStatusCode().value());
        assertEquals(409, handler.shopProblem(new ProductInUseException("Rice 25kg"), request).getStatusCode().value());
        ResponseEntity<ErrorResponse> bad = handler.shopProblem(new InvalidInputException("Quantity must be at least 1."), request);
        assertEquals(400, bad.getStatusCode().value());
        assertEquals("Bad Request", bad.getBody().error());
    }

    @Test
    void aDatabaseFaultIsA500ThatHidesTheDetails() {
        DatabaseException fault = new DatabaseException("Cannot save the sale", new SQLException("Table SALE not found; file C:/secret/shop.mv.db"));
        ResponseEntity<ErrorResponse> response = handler.shopProblem(fault, request);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Something went wrong on the server.", response.getBody().message());
        assertFalse(response.getBody().toString().contains("secret"));
    }

    @Test
    void aClashInTheDatabaseIsA409() {
        ResponseEntity<ErrorResponse> response = handler.clash(new DataIntegrityViolationException("Unique index violated: NAME"), request);
        assertEquals(409, response.getStatusCode().value());
        assertFalse(response.getBody().message().contains("NAME"));
    }

    @Test
    void anyOtherDatabaseTroubleAndAnyBugAre500WithAPoliteMessage() {
        ResponseEntity<ErrorResponse> database = handler.databaseFault(new QueryTimeoutException("timeout on host db01"), request);
        ResponseEntity<ErrorResponse> bug = handler.bug(new NullPointerException("shop.getName() is null in ProductService line 42"), request);
        for (ResponseEntity<ErrorResponse> response : List.of(database, bug)) {
            assertEquals(500, response.getStatusCode().value());
            assertEquals("Something went wrong on the server.", response.getBody().message());
            assertTrue(response.getBody().problems().isEmpty());
        }
    }

    @Test
    void theWrongMethodIs405AndTheAllowHeaderListsWhatWorks() {
        ResponseEntity<ErrorResponse> response = handler.wrongMethod(
                new HttpRequestMethodNotSupportedException("DELETE", Set.of("POST", "GET")), new MockHttpServletRequest("DELETE", "/products"));
        assertEquals(405, response.getStatusCode().value());
        assertEquals("GET, POST", response.getHeaders().getFirst("Allow"));
        assertEquals("DELETE is not allowed here. Use GET, POST.", response.getBody().message());
    }

    @Test
    void anUnknownPathIsA404AndWrongContentTypeIsA415() {
        MockHttpServletRequest nothing = new MockHttpServletRequest("GET", "/nothing");
        assertEquals("No such path: /nothing", handler.noSuchPath(nothing).getBody().message());
        assertEquals(415, handler.wrongContentType(request).getStatusCode().value());
    }
}
