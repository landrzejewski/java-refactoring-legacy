package pl.training.workshop.m5.s27_templatetostrategy.step3;

import java.util.Locale;

import pl.training.workshop.shared.Money;

/** Krok 3: bez zmian - format tekstowy w jednym miejscu (był skopiowany w DailyTextReport i WeeklyTextReport). */
public final class TextFormat implements ReportFormat {
    @Override
    public String header(String label) {
        return "RAPORT " + label.toUpperCase(Locale.ROOT) + "\n" + "-".repeat(37) + "\n";
    }

    @Override
    public String row(String title, int tickets, Money revenue) {
        return String.format(Locale.ROOT, "%-20s %5d %10s", title, tickets, revenue) + "\n";
    }

    @Override
    public String footer(int tickets, Money revenue) {
        return "-".repeat(37) + "\n" + row("RAZEM", tickets, revenue);
    }
}
