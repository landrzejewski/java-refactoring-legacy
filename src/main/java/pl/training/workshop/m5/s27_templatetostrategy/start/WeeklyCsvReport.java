package pl.training.workshop.m5.s27_templatetostrategy.start;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import pl.training.workshop.shared.Money;

/** Start: okres tygodniowy (poniedziałek-niedziela) + format CSV skopiowany z DailyCsvReport. */
public final class WeeklyCsvReport extends ScreeningReport {
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
        return "# raport " + label + "\nfilm;bilety;przychod\n";
    }

    @Override
    protected String row(String title, int tickets, Money revenue) {
        String cell = title.contains(";") ? "\"" + title + "\"" : title;
        return cell + ";" + tickets + ";" + revenue + "\n";
    }

    @Override
    protected String footer(int tickets, Money revenue) {
        return "SUMA;" + tickets + ";" + revenue + "\n";
    }
}
