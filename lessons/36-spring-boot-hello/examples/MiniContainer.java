import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "Where do the objects come from?" This is the idea at the heart of Spring in about 60 lines: a CONTAINER that creates
 * objects (beans) and passes each one the other objects it needs through its constructor (dependency injection).
 * It uses reflection, Java's ability to inspect classes while the program runs. Spring does the same, with many more rules.
 *
 * Run:  java MiniContainer.java
 */
public class MiniContainer {

    /** Our own little @Component: a marker that says "container, please manage this class". */
    @Retention(RetentionPolicy.RUNTIME) // keep the marker at run time, or reflection could not see it
    @interface Component {}

    // ---- the app: three classes, each asking for what it needs in its constructor ----

    @Component
    static class PriceList {
        PriceList() {
            System.out.println("  creating PriceList");
        }

        int priceOf(String product) {
            return product.equals("rice") ? 1250 : 98;
        }
    }

    @Component
    static class Cashier {
        private final PriceList prices;

        Cashier(PriceList prices) { // "I need a PriceList": the container finds or makes one
            this.prices = prices;
            System.out.println("  creating Cashier (got " + prices.getClass().getSimpleName() + ")");
        }

        int total(String product, int quantity) {
            return prices.priceOf(product) * quantity;
        }
    }

    @Component
    static class SalesController {
        final Cashier cashier;
        final PriceList prices;

        SalesController(Cashier cashier, PriceList prices) {
            this.cashier = cashier;
            this.prices = prices;
            System.out.println("  creating SalesController");
        }
    }

    // ---- the container ----

    private final Map<Class<?>, Object> beans = new HashMap<>(); // one object per class: "singleton" beans

    /** The bean of this type; created (with everything it needs) the first time somebody asks. */
    <T> T get(Class<T> type) throws Exception {
        if (!type.isAnnotationPresent(Component.class)) {
            throw new IllegalArgumentException(type.getSimpleName() + " is not a @Component");
        }
        Object existing = beans.get(type);
        if (existing != null) {
            return type.cast(existing); // the SAME object every time
        }
        Constructor<?> constructor = type.getDeclaredConstructors()[0];
        List<Object> arguments = new ArrayList<>();
        for (Class<?> needed : constructor.getParameterTypes()) {
            arguments.add(get(needed)); // build the dependencies first
        }
        constructor.setAccessible(true);
        Object bean = constructor.newInstance(arguments.toArray());
        beans.put(type, bean);
        return type.cast(bean);
    }

    public static void main(String[] args) throws Exception {
        MiniContainer container = new MiniContainer();

        System.out.println("Asking for a SalesController:");
        SalesController controller = container.get(SalesController.class);

        System.out.println("The total for 2 bags of rice: " + controller.cashier.total("rice", 2));
        System.out.println("Same Cashier every time? " + (container.get(Cashier.class) == controller.cashier));
        System.out.println("Cashier and controller share one PriceList? " + (controller.prices == container.get(PriceList.class)));

        try {
            container.get(String.class);
        } catch (IllegalArgumentException e) {
            System.out.println("Refused: " + e.getMessage());
        }
    }
}
