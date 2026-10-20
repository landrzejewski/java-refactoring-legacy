package pl.training.workshop.m5.s27_templatetostrategy.start;

import java.time.LocalDate;
import java.util.Locale;

import pl.training.workshop.shared.Money;

/** Start: okres dzienny skopiowany z DailyCsvReport + format tekstowy z kolumnami. */
public final class DailyTextReport extends ScreeningReport {
    @Override
    protected boolean includes(LocalDate day, LocalDate saleDay) {
        return saleDay.equals(day);
    }

    @Override
    protected String label(LocalDate day) {
        return "dzien " + day;
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
