package dev.javabybuilding.exercises;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Lesson 20, exercise 2: dates and times with java.time. Nothing here reads the real clock, so every answer is repeatable.
 * Useful: LocalTime.of(8, 0), time.isBefore(...), ChronoUnit.DAYS.between(a, b), date.getDayOfWeek(), date.plusDays(n),
 * TemporalAdjusters.lastDayOfMonth(), time.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US)).
 */
public class TimeDrills {

    /** TODO: the shop opens at 08:00 (included) and closes at 20:00 (not included). */
    public static boolean isShopOpen(LocalTime time) {
        return false;
    }

    /** TODO: whole days from one date to another (negative when the second date is earlier). */
    public static long daysBetween(LocalDate from, LocalDate to) {
        return -999;
    }

    /** TODO: true on Saturday and Sunday (DayOfWeek.SATURDAY, DayOfWeek.SUNDAY). */
    public static boolean isWeekend(LocalDate date) {
        return false;
    }

    /** TODO: the date an invoice is due: the invoice date plus the payment term in days. */
    public static LocalDate dueDate(LocalDate invoiceDate, int termDays) {
        return null;
    }

    /** TODO: the last day of the month of this date: 2026-02-10 gives 2026-02-28. */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        return null;
    }

    /** TODO: a time as text for a receipt: 9:05 AM or 5:30 PM (no leading zero on the hour, AM/PM in capitals). */
    public static String clockText(LocalTime time) {
        return null;
    }
}
