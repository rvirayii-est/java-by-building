package dev.javabybuilding.inventory;

/**
 * A Discount is an object; a file can only hold text. These two methods translate between them:
 * NoDiscount is "none", PercentDiscount(10) is "percent:10", FixedDiscount(5000) is "fixed:5000" (centavos),
 * SeniorPwdDiscount is "senior".
 */
public class DiscountCodes {

    /** The text to write in a file for this discount. */
    public static String encode(Discount discount) {
        if (discount instanceof PercentDiscount percent) { // "instanceof ... percent" checks the kind AND names it
            return "percent:" + percent.getPercent();
        } else if (discount instanceof FixedDiscount fixed) {
            return "fixed:" + fixed.getAmountCentavos();
        } else if (discount instanceof SeniorPwdDiscount) {
            return "senior";
        }
        return "none";
    }

    /**
     * The discount for a code written by encode.
     *
     * @throws InvalidInputException when the code is unknown or its number is not valid (the discount constructors check)
     */
    public static Discount decode(String code) {
        String text = code.trim();
        if (text.equals("none")) {
            return new NoDiscount();
        } else if (text.equals("senior")) {
            return new SeniorPwdDiscount();
        } else if (text.startsWith("percent:")) {
            return new PercentDiscount(number(text, "percent:"));
        } else if (text.startsWith("fixed:")) {
            return new FixedDiscount(number(text, "fixed:"));
        }
        throw new InvalidInputException("Unknown discount code: " + code);
    }

    private static int number(String text, String prefix) {
        try {
            return Integer.parseInt(text.substring(prefix.length()));
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Unknown discount code: " + text);
        }
    }
}
