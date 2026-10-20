package pl.training.workshop.m5.s27_templatetostrategy.step3;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/** Krok 3: tydzień poniedziałek-niedziela w jednym miejscu (był skopiowany w obu Weekly*Report). */
public final class WeeklyPeriod implements ReportPeriod {
    @Override
    public boolean includes(LocalDate day, LocalDate saleDay) {
        LocalDate monday = monday(day);
        return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
    }

    @Override
    public String label(LocalDate day) {
        LocalDate monday = monday(day);
        return "tydzien " + monday + ".." + monday.plusDays(6);
    }

    private static LocalDate monday(LocalDate day) {
        return day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
