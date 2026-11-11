package dev.javabybuilding.inventory;

/**
 * The 20% discount for senior citizens and persons with disability (PWD).
 * shortcut: the real rule has more conditions (which items, a purchase cap, ID checks); this teaches the idea only.
 * It reuses PercentDiscount (composition) instead of copying its arithmetic.
 */
public class SeniorPwdDiscount implements Discount {

    private final PercentDiscount twentyPercent = new PercentDiscount(20);

    /** TODO 3: reuse the field: return what twentyPercent.discountFor(...) says. */
    @Override
    public int discountFor(int subtotalCentavos) {
        return -1;
    }

    @Override
    public String label() {
        return "Senior/PWD 20%";
    }
}
