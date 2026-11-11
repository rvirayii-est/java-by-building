import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * How can an annotation such as @NotBlank DO anything? An annotation is only a label. Something has to read the labels at run
 * time and act on them. This is a 60-line version of what Bean Validation (Hibernate Validator) does: it looks at every field
 * of a record, finds the labels, checks the value, and collects ALL the problems, not just the first.
 *
 * Run:  java MiniValidator.java
 */
public class MiniValidator {

    // ---- our own two annotations (labels) ----

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.RECORD_COMPONENT)
    @interface NotBlank {
        String message() default "is required";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.RECORD_COMPONENT)
    @interface Min {
        long value();

        String message() default "is too small";
    }

    // ---- something to check: a request with labels on its fields ----

    record NewProduct(@NotBlank String name, @Min(value = 1, message = "must be at least 1") Integer quantity) {}

    // ---- the validator: reads the labels with reflection ----

    /** Every problem as "field: message", in the order of the fields. An empty list means the object is valid. */
    static List<String> validate(Record request) throws Exception {
        List<String> problems = new ArrayList<>();
        for (RecordComponent field : request.getClass().getRecordComponents()) {
            Object value = field.getAccessor().invoke(request);
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            if (notBlank != null && (value == null || value.toString().isBlank())) {
                problems.add(field.getName() + ": " + notBlank.message());
                continue; // one problem per field is enough
            }
            Min min = field.getAnnotation(Min.class);
            if (min != null && value instanceof Number number && number.longValue() < min.value()) {
                problems.add(field.getName() + ": " + min.message());
            }
        }
        return problems;
    }

    public static void main(String[] args) throws Exception {
        show(new NewProduct("Salt 1kg", 5));
        show(new NewProduct("   ", 0));
        show(new NewProduct(null, null));

        // The same shape for every failure: this is what the API's ErrorResponse carries in its "problems" list.
        List<String> problems = validate(new NewProduct("", -3));
        StringBuilder json = new StringBuilder("{\"status\":400,\"message\":\"The request has invalid fields.\",\"problems\":[");
        for (int i = 0; i < problems.size(); i++) {
            String[] parts = problems.get(i).split(": ", 2);
            json.append(i > 0 ? "," : "").append("{\"field\":\"").append(parts[0]).append("\",\"message\":\"").append(parts[1]).append("\"}");
        }
        System.out.println(json.append("]}"));
    }

    static void show(NewProduct request) throws Exception {
        List<String> problems = validate(request);
        System.out.println(request + " -> " + (problems.isEmpty() ? "valid" : problems));
    }
}
