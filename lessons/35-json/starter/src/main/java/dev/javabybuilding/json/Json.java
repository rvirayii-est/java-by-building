package dev.javabybuilding.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.javabybuilding.inventory.InvalidInputException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The shop's one JSON reader and writer. Jackson's {@code ObjectMapper} does the real work; this class only decides how it
 * is set up, once, so every part of the app writes dates and money the same way.
 *
 * <p>Words to know: to <b>serialise</b> is to turn a Java object into JSON text; to <b>deserialise</b> is the reverse.
 */
public final class Json {

    // TODO 1: set the mapper up once. JsonMapper.builder() ... .build() with
    //   .addModule(new JavaTimeModule())                              (so LocalDateTime works)
    //   .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)      (dates as "2026-03-01T09:30:00")
    //   .enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)       (1250.50, never 1.2505E+3)
    //   .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)    (98.50 in a Map stays exact)
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Json() {}

    /** The compact JSON text for a value: {@code {"name":"Rice 25kg","price":1250.50}}. */
    public static String write(Object value) {
        // TODO 2: MAPPER.writeValueAsString(value). It throws a checked JsonProcessingException: catch it and throw
        // new IllegalStateException("Cannot write JSON: " + e.getOriginalMessage(), e) (a failure here is OUR bug, not the caller's).
        throw new UnsupportedOperationException("TODO 2");
    }

    /** The same, spread over several lines so a person can read it. */
    public static String writePretty(Object value) {
        // TODO 2 (continued): MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value), same exception handling.
        throw new UnsupportedOperationException("TODO 2");
    }

    /**
     * Reads JSON text into an object of the given type (a record, a Map ...).
     *
     * @throws InvalidInputException when the text is not valid JSON, has a field the type does not know, or a value of the wrong kind
     */
    public static <T> T read(String json, Class<T> type) {
        // TODO 3: MAPPER.readValue(json, type); catch JsonProcessingException e and throw new InvalidInputException(describe(e)).
        throw new UnsupportedOperationException("TODO 3");
    }

    /** Reads JSON text that must be a JSON object ({...}) into a Map. */
    public static Map<String, Object> readObject(String json) {
        // TODO 3 (continued): MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {}), same exception handling.
        throw new UnsupportedOperationException("TODO 3");
    }

    /**
     * Turns data that is already in Java (a Map read from JSON) into a request record: {@code convert(map, CreateProductRequest.class)}.
     * A missing field becomes null (or 0 for an int); an unknown field is refused.
     *
     * @throws InvalidInputException naming the field when a value has the wrong kind
     */
    public static <T> T convert(Map<String, Object> data, Class<T> type) {
        // TODO 4: MAPPER.convertValue(data, type) (use an empty Map when data is null). It reports problems as an
        // IllegalArgumentException whose getCause() is a JsonMappingException: throw new InvalidInputException(describe(thatCause)).
        // For any other cause throw new InvalidInputException("Invalid request body").
        throw new UnsupportedOperationException("TODO 4");
    }

    /** A short sentence for a person: the field path and what was wrong, without Jackson's long source dump. */
    private static String describe(JsonProcessingException e) {
        if (e instanceof JsonMappingException mapping && !mapping.getPath().isEmpty()) {
            String field = mapping.getPath().stream().map(reference -> reference.getFieldName()).collect(Collectors.joining("."));
            return "Invalid value for '" + field + "': " + mapping.getOriginalMessage();
        }
        return "Malformed JSON: " + e.getOriginalMessage();
    }
}
