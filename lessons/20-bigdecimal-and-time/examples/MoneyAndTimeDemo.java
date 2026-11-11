// Run with:  java MoneyAndTimeDemo.java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MoneyAndTimeDemo {

    public static void main(String[] args) {
        // ---------- money ----------
        System.out.println("double:     " + (0.1 + 0.2));
        System.out.println("BigDecimal: " + new BigDecimal("0.1").add(new BigDecimal("0.2")));
        System.out.println("From a double: " + new BigDecimal(0.1).toString().substring(0, 24) + "...");

        BigDecimal two = new BigDecimal("2.0");
        BigDecimal twoExactly = new BigDecimal("2.00");
        System.out.println("equals: " + two.equals(twoExactly) + ", compareTo: " + two.compareTo(twoExactly));

        BigDecimal price = new BigDecimal("1250.50");
        BigDecimal total = price.multiply(BigDecimal.valueOf(3)); // BigDecimal never changes: multiply returns a new number
        System.out.println("3 x " + price + " = " + total);
        BigDecimal vat = total.multiply(new BigDecimal("12")).divide(new BigDecimal("112"), 2, RoundingMode.HALF_UP);
        System.out.println("VAT inside it: " + vat);
        try {
            BigDecimal.ONE.divide(new BigDecimal("3"));
        } catch (ArithmeticException e) {
            System.out.println("1 / 3 without a rounding rule: " + e.getMessage());
        }
        System.out.println("1 / 3 rounded: " + BigDecimal.ONE.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP));
        System.out.println(String.format(Locale.US, "Formatted: PHP %,.2f", new BigDecimal("1234567.5")));

        // ---------- dates and times ----------
        LocalDate day = LocalDate.of(2026, 2, 27);
        System.out.println("Date: " + day + " is a " + day.getDayOfWeek() + ", +2 days is " + day.plusDays(2));
        System.out.println("Period: " + Period.between(day, LocalDate.of(2026, 5, 1)));

        LocalTime open = LocalTime.of(8, 0);
        LocalTime now = LocalTime.of(17, 45);
        System.out.println("Open for " + Duration.between(open, now).toHours() + " hours " + Duration.between(open, now).toMinutesPart() + " minutes");

        LocalDateTime sale = LocalDateTime.of(2026, 3, 1, 9, 30);
        System.out.println("Sale at " + sale + " -> " + sale.format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a", Locale.US)));

        // A Clock is where "now" comes from. Clock.fixed always says the same time, so this output never changes.
        ZoneId manila = ZoneId.of("Asia/Manila");
        Clock fixed = Clock.fixed(Instant.parse("2026-02-28T23:30:00Z"), manila);
        System.out.println("In Manila:   " + LocalDateTime.now(fixed));
        System.out.println("In UTC:      " + LocalDateTime.now(fixed.withZone(ZoneId.of("UTC"))));
        System.out.println("With a zone: " + ZonedDateTime.now(fixed));

        // Parsing text into a date, and what a bad one looks like.
        System.out.println("Parsed: " + LocalDate.parse("2026-03-01").getMonth());
        try {
            LocalDate.parse("1 March 2026");
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("Cannot parse: " + e.getParsedString());
        }
    }
}
