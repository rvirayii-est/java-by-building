import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * A tour of JSON and Jackson, the Java library that turns objects into JSON text and back.
 * Needs the Jackson jars on the class path: run it with  bash scripts/run-examples.sh  (it finds them for you),
 * or see lesson 35, section 3, for the command.
 */
public class JsonTour {

    // A record is perfect for JSON: its fields are its components. Jackson reads and writes records out of the box.
    record Product(String name, BigDecimal price, int quantity) {}

    record Receipt(int number, LocalDateTime soldAt, List<Product> items) {}

    // An ordinary class: Jackson uses the getters. EVERY getter becomes a JSON field, whether you meant it or not.
    static class Customer {
        private final String name = "Ana Reyes";
        private final String phone = "09171234567";

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public boolean isVip() { // a helper method that looks like a getter
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        System.out.println("== 1. Object to JSON (serialise)");
        Product rice = new Product("Rice 25kg", new BigDecimal("1250.50"), 12);
        System.out.println(mapper.writeValueAsString(rice));

        System.out.println("== 2. JSON to object (deserialise)");
        Product back = mapper.readValue("{\"name\":\"Sugar 1kg\",\"price\":75.00,\"quantity\":0}", Product.class);
        System.out.println(back + "   price keeps its decimals: " + back.price());

        System.out.println("== 3. A list and a Map");
        System.out.println(mapper.writeValueAsString(List.of(rice, back)));
        System.out.println(mapper.writeValueAsString(Map.of("status", "LOW")));

        System.out.println("== 4. Dates need a module");
        Receipt receipt = new Receipt(1, LocalDateTime.of(2026, 3, 1, 9, 30), List.of(rice));
        try {
            mapper.writeValueAsString(receipt);
        } catch (JsonProcessingException e) {
            System.out.println("without the module: " + e.getClass().getSimpleName());
        }
        ObjectMapper withDates = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println(withDates.writeValueAsString(receipt));

        System.out.println("== 5. Pretty printing");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rice));

        System.out.println("== 6. Bad input is an exception, not a guess");
        try {
            mapper.readValue("{\"name\":\"Rice\",\"cost\":1}", Product.class);
        } catch (JsonProcessingException e) {
            System.out.println("unknown field: " + e.getOriginalMessage().split("\\(")[0].trim());
        }
        try {
            mapper.readValue("{\"quantity\":\"many\"}", Product.class);
        } catch (JsonProcessingException e) {
            System.out.println("wrong kind: " + e.getOriginalMessage().split(":")[0]);
        }
        try {
            mapper.readValue("{\"name\":", Product.class);
        } catch (JsonProcessingException e) {
            System.out.println("broken text: " + e.getClass().getSimpleName());
        }

        System.out.println("== 7. Careful: every getter is published");
        System.out.println(mapper.writeValueAsString(new Customer()));
        System.out.println("(nobody decided to publish the phone number, and 'vip' came from a helper method)");
    }
}
