package dev.javabybuilding.exercises;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/** Lesson 20, exercise 2: dates and times with java.time. Nothing here reads the real clock, so every answer is repeatable. */
public class TimeDrills {

    /** The shop opens at 08:00 (included) and closes at 20:00 (not included). */
    public static boolean isShopOpen(LocalTime time) {
        return !time.isBefore(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(20, 0));
    }

    /** Whole days from one date to another (negative when the second date is earlier). */
    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    /** True on Saturday and Sunday. */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /** The date an invoice is due: the invoice date plus the payment term in days. */
    public static LocalDate dueDate(LocalDate invoiceDate, int termDays) {
        return invoiceDate.plusDays(termDays);
    }

    /** The last day of the month of this date: 2026-02-10 gives 2026-02-28. */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /** A time as text for a receipt: 9:05 AM or 5:30 PM (no leading zero on the hour, AM/PM in capitals). */
    public static String clockText(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US));
    }
}
