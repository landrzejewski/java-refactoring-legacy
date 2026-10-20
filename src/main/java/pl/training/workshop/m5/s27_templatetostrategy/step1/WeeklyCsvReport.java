package pl.training.workshop.m5.s27_templatetostrategy.step1;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/** Krok 1: podklasa wybiera format przez konstruktor; zostały w niej tylko haki okresu. */
public final class WeeklyCsvReport extends ScreeningReport {
    public WeeklyCsvReport() {
        super(new CsvFormat());
    }

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
}
