package dev.javabybuilding.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/** A plain unit test of the filter: no Spring, no server. Mock request, mock response, a chain that records what it saw. */
class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    /** Runs the filter; returns the id the code INSIDE the chain could see in the MDC. */
    private String run(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
        List<String> seen = new ArrayList<>();
        filter.doFilter(request, response, new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                seen.add(MDC.get("requestId"));
            }
        });
        return seen.get(0);
    }

    @Test
    void aMissingIdIsGeneratedAndReturnedAndVisibleToTheCodeInside() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String inside = run(new MockHttpServletRequest("GET", "/products"), response);
        assertEquals(inside, response.getHeader("X-Request-Id"));
        assertTrue(inside.matches("[0-9a-f-]{36}"), "a UUID: " + inside);
    }

    @Test
    void aPlainIdFromTheCallerIsReused() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/products");
        request.addHeader("X-Request-Id", "order-4711");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertEquals("order-4711", run(request, response));
        assertEquals("order-4711", response.getHeader("X-Request-Id"));
    }

    @Test
    void anIdThatCouldForgeALogLineOrIsTooLongIsReplaced() throws Exception {
        for (String evil : new String[] {"abc\nINFO fake line", "has space", "x".repeat(65), "<script>", ""}) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/products");
            request.addHeader("X-Request-Id", evil);
            MockHttpServletResponse response = new MockHttpServletResponse();
            String id = run(request, response);
            assertNotEquals(evil, id);
            assertTrue(id.matches("[0-9a-f-]{36}"), id);
        }
    }

    @Test
    void theMdcIsCleanedSoTheNextRequestOnTheSameThreadStartsFresh() throws Exception {
        run(new MockHttpServletRequest("GET", "/products"), new MockHttpServletResponse());
        assertNull(MDC.get("requestId"));
    }

    @Test
    void theMdcIsCleanedEvenWhenTheRequestBlowsUp() {
        try {
            filter.doFilter(new MockHttpServletRequest("GET", "/boom"), new MockHttpServletResponse(), new MockFilterChain() {
                @Override
                public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                    throw new IllegalStateException("boom");
                }
            });
        } catch (Exception expected) {
            assertEquals("boom", expected.getMessage());
        }
        assertNull(MDC.get("requestId"));
    }
}
