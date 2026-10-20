package pl.training.workshop.m5.s27_templatetostrategy.start;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import pl.training.workshop.shared.Money;

/** Start: okres tygodniowy skopiowany z WeeklyCsvReport + format tekstowy skopiowany z DailyTextReport. */
public final class WeeklyTextReport extends ScreeningReport {
    @Override
    protected boolean includes(LocalDate day, LocalDate saleDay) {
        LocalDate monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
    }

    @Override
    protected String label(LocalDate day) {
        LocalDate monday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return "tydzien " + monday + ".." + monday.plusDays(6);
    }

    @Override
    protected String header(String label) {
        return "RAPORT " + label.toUpperCase(Locale.ROOT) + "\n" + "-".repeat(37) + "\n";
    }

    @Override
    protected String row(String title, int tickets, Money revenue) {
        return String.format(Locale.ROOT, "%-20s %5d %10s", title, tickets, revenue) + "\n";
    }

    @Override
    protected String footer(int tickets, Money revenue) {
        return "-".repeat(37) + "\n" + row("RAZEM", tickets, revenue);
    }
}
