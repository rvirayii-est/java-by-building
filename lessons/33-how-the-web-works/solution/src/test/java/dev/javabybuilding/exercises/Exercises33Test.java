package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Exercises33Test {

    @Test
    void parseRequestLineReadsThreeParts() {
        HttpDrills.RequestLine line = HttpDrills.parseRequestLine("GET /products?status=LOW HTTP/1.1");
        assertEquals("GET", line.method());
        assertEquals("/products?status=LOW", line.target());
        assertEquals("HTTP/1.1", line.version());
    }

    @Test
    void parseRequestLineRejectsBadLines() {
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseRequestLine("GET /products"));
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseRequestLine("GET /a b HTTP/1.1"));
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseRequestLine("get /products HTTP/1.1"));
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseRequestLine("GET /products FTP/1.0"));
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseRequestLine(""));
    }

    @Test
    void statusClassNamesTheKindOfAnswer() {
        assertEquals("Informational", HttpDrills.statusClass(100));
        assertEquals("Success", HttpDrills.statusClass(201));
        assertEquals("Redirect", HttpDrills.statusClass(301));
        assertEquals("Client error", HttpDrills.statusClass(404));
        assertEquals("Server error", HttpDrills.statusClass(500));
        assertEquals("Server error", HttpDrills.statusClass(599));
    }

    @Test
    void statusClassRejectsNumbersOutsideTheRange() {
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.statusClass(99));
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.statusClass(600));
    }

    @Test
    void parseHeaderLowercasesTheNameAndTrimsTheValue() {
        var header = HttpDrills.parseHeader("Content-Type:  text/plain ");
        assertEquals("content-type", header.getKey());
        assertEquals("text/plain", header.getValue());
    }

    @Test
    void parseHeaderSplitsOnTheFirstColonOnly() {
        var header = HttpDrills.parseHeader("Host: localhost:8080");
        assertEquals("host", header.getKey());
        assertEquals("localhost:8080", header.getValue());
    }

    @Test
    void parseHeaderRejectsLinesWithoutAName() {
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseHeader("no colon here"));
        assertThrows(IllegalArgumentException.class, () -> HttpDrills.parseHeader(": value"));
    }

    @Test
    void buildRequestMakesTheTextOfASimpleGet() {
        assertEquals("GET /health HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n",
                HttpDrills.buildRequest("GET", "/health", "localhost"));
    }

    @Test
    void safeMethodsOnlyRead() {
        assertTrue(HttpDrills.isSafe("GET"));
        assertTrue(HttpDrills.isSafe("HEAD"));
        assertTrue(HttpDrills.isSafe("OPTIONS"));
        assertFalse(HttpDrills.isSafe("POST"));
        assertFalse(HttpDrills.isSafe("DELETE"));
        assertFalse(HttpDrills.isSafe("get"));
    }
}
