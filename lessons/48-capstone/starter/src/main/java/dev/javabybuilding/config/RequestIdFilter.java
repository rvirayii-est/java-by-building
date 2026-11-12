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
 *   <li>An id sent by the caller is reused (so a chain of services can share one), but only if it is short and plain: a header is
 *       user input, and an id with a newline in it could forge log lines.</li>
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
        return incoming != null && PLAIN.matcher(incoming).matches() ? incoming : UUID.randomUUID().toString();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String id = idFor(request.getHeader(HEADER));
        MDC.put("requestId", id);
        response.setHeader(HEADER, id);
        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long millis = (System.nanoTime() - start) / 1_000_000;
            boolean probe = request.getRequestURI().startsWith("/actuator/health"); // platforms call this every few seconds: keep it quiet
            String line = request.getMethod() + " " + request.getRequestURI() + " -> " + response.getStatus() + " in " + millis + " ms";
            if (probe) {
                LOG.debug(line);
            } else {
                LOG.info(line);
            }
            MDC.remove("requestId"); // a server thread is reused for the next request: never leave the id behind
        }
    }
}
