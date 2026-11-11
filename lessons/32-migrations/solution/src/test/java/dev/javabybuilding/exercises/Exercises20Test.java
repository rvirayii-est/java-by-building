package dev.javabybuilding.exercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class Exercises20Test {

    private static BigDecimal peso(String text) {
        return new BigDecimal(text);
    }

    @Test
    void addVatAddsTwelvePercentRoundedHalfUp() {
        assertEquals(peso("112.00"), MoneyDrills.addVat(peso("100.00")));
        assertEquals(peso("22.39"), MoneyDrills.addVat(peso("19.99"))); // 22.3888
        assertEquals(peso("0.06"), MoneyDrills.addVat(peso("0.05"))); // 0.056
    }

    @Test
    void shareRoundsDownSoNobodyPaysMoreThanTheirTurn() {
        assertEquals(peso("33.33"), MoneyDrills.share(peso("100.00"), 3));
        assertEquals(peso("50.00"), MoneyDrills.share(peso("100.00"), 2));
        assertEquals(peso("0.33"), MoneyDrills.share(peso("1.00"), 3));
    }

    @Test
    void sameAmountIgnoresTheScale() {
        assertTrue(MoneyDrills.sameAmount(peso("2.0"), peso("2.00")));
        assertFalse(MoneyDrills.sameAmount(peso("2.00"), peso("2.01")));
        assertFalse(peso("2.0").equals(peso("2.00"))); // this is why we do not use equals for money
    }

    @Test
    void sumStartsFromZeroPointZeroZero() {
        assertEquals(peso("0.60"), MoneyDrills.sum(List.of(peso("0.10"), peso("0.20"), peso("0.30"))));
        assertEquals(peso("0.00"), MoneyDrills.sum(List.of()));
    }

    @Test
    void changeIsPaidMinusPriceAndRefusesShortPayments() {
        assertEquals(peso("27.50"), MoneyDrills.change(peso("72.50"), peso("100.00")));
        assertEquals(peso("0.00"), MoneyDrills.change(peso("50.00"), peso("50.00")));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> MoneyDrills.change(peso("50.00"), peso("45.25")));
        assertEquals("Not enough money: short by 4.75", e.getMessage());
    }

    @Test
    void theShopIsOpenFromEightUntilJustBeforeEight() {
        assertFalse(TimeDrills.isShopOpen(LocalTime.of(7, 59)));
        assertTrue(TimeDrills.isShopOpen(LocalTime.of(8, 0)));
        assertTrue(TimeDrills.isShopOpen(LocalTime.of(19, 59)));
        assertFalse(TimeDrills.isShopOpen(LocalTime.of(20, 0)));
    }

    @Test
    void daysBetweenCountsWholeDaysAcrossMonthsAndLeapYears() {
        assertEquals(30, TimeDrills.daysBetween(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)));
        assertEquals(2, TimeDrills.daysBetween(LocalDate.of(2028, 2, 28), LocalDate.of(2028, 3, 1))); // 2028 is a leap year
        assertEquals(-5, TimeDrills.daysBetween(LocalDate.of(2026, 3, 6), LocalDate.of(2026, 3, 1)));
    }

    @Test
    void weekendsAreSaturdayAndSunday() {
        assertTrue(TimeDrills.isWeekend(LocalDate.of(2026, 3, 7)));  // Saturday
        assertTrue(TimeDrills.isWeekend(LocalDate.of(2026, 3, 8)));  // Sunday
        assertFalse(TimeDrills.isWeekend(LocalDate.of(2026, 3, 9))); // Monday
    }

    @Test
    void dueDateAndLastDayOfMonth() {
        assertEquals(LocalDate.of(2026, 3, 3), TimeDrills.dueDate(LocalDate.of(2026, 2, 21), 10));
        assertEquals(LocalDate.of(2026, 2, 28), TimeDrills.lastDayOfMonth(LocalDate.of(2026, 2, 10)));
        assertEquals(LocalDate.of(2028, 2, 29), TimeDrills.lastDayOfMonth(LocalDate.of(2028, 2, 10)));
    }

    @Test
    void clockTextHasNoLeadingZeroAndCapitalAmPm() {
        assertEquals("9:05 AM", TimeDrills.clockText(LocalTime.of(9, 5)));
        assertEquals("5:30 PM", TimeDrills.clockText(LocalTime.of(17, 30)));
        assertEquals("12:00 PM", TimeDrills.clockText(LocalTime.NOON));
    }
}
