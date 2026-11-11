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

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())                                  // teaches Jackson LocalDateTime and friends
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)          // "2026-03-01T09:30:00", not [2026,3,1,9,30]
            .enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)           // 1250.50, never 1.2505E+3
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)        // 98.50 in a Map stays exact, not a double
            .build();                                                          // unknown JSON fields are refused (the default)

    private Json() {}

    /** The compact JSON text for a value: {@code {"name":"Rice 25kg","price":1250.50}}. */
    public static String write(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot write JSON: " + e.getOriginalMessage(), e); // a bug in our code, not the caller's input
        }
    }

    /** The same, spread over several lines so a person can read it. */
    public static String writePretty(Object value) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot write JSON: " + e.getOriginalMessage(), e);
        }
    }

    /**
     * Reads JSON text into an object of the given type (a record, a Map ...).
     *
     * @throws InvalidInputException when the text is not valid JSON, has a field the type does not know, or a value of the wrong kind
     */
    public static <T> T read(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new InvalidInputException(describe(e));
        }
    }

    /** Reads JSON text that must be a JSON object ({...}) into a Map. */
    public static Map<String, Object> readObject(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new InvalidInputException(describe(e));
        }
    }

    /**
     * Turns data that is already in Java (a Map read from JSON) into a request record: {@code convert(map, CreateProductRequest.class)}.
     * A missing field becomes null (or 0 for an int); an unknown field is refused.
     *
     * @throws InvalidInputException naming the field when a value has the wrong kind
     */
    public static <T> T convert(Map<String, Object> data, Class<T> type) {
        try {
            return MAPPER.convertValue(data == null ? Map.of() : data, type);
        } catch (IllegalArgumentException e) { // convertValue reports problems this way, with the JsonProcessingException inside
            if (e.getCause() instanceof JsonMappingException mapping) {
                throw new InvalidInputException(describe(mapping));
            }
            throw new InvalidInputException("Invalid request body");
        }
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
