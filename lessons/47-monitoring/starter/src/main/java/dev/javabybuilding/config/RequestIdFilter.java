package dev.javabybuilding.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Gives every request an id and writes ONE log line when it ends. When a customer says "my sale failed at 14:03", the id on the
 * response header (X-Request-Id) finds every log line of that request, and nobody else's.
 *
 * <ul>
 *   <li>An id sent by the caller is reused, but only if it is short and plain (letters, digits, '.', '_', '-', 1 to 64 characters):
 *       a header is user input, and an id with a newline in it could forge log lines.</li>
 *   <li>The id goes into the MDC (a per-thread map the logger reads), so EVERY line logged while handling the request has it.</li>
 *   <li>It runs FIRST ({@code HIGHEST_PRECEDENCE}), before Spring Security, so even a refused request (401) has an id and a log line.</li>
 * </ul>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";
    private static final Pattern PLAIN = Pattern.compile("[A-Za-z0-9._-]{1,64}");
    private static final Logger LOG = LoggerFactory.getLogger("http");

    /** The caller's id when it is plain, otherwise a new random one. */
    static String idFor(String incoming) {
        // TODO 1a: incoming != null && PLAIN.matcher(incoming).matches() ? incoming : UUID.randomUUID().toString()
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // TODO 1b: 1. id = idFor(request.getHeader(HEADER));  MDC.put("requestId", id);  response.setHeader(HEADER, id);
        //          2. remember the start time (System.nanoTime()) and call chain.doFilter(request, response) inside try { ... } finally { ... }
        //          3. in finally: log  METHOD URI -> STATUS in N ms  with LOG.info (use LOG.debug when the URI starts with /actuator/health),
        //             then MDC.remove("requestId")  (a server thread is reused: never leave the id behind).
        chain.doFilter(request, response);
    }
}
