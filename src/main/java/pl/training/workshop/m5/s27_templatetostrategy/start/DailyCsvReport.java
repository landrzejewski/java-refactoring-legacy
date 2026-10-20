package pl.training.workshop.m5.s27_templatetostrategy.start;

import java.time.LocalDate;

import pl.training.workshop.shared.Money;

/** Start: okres dzienny + format CSV. */
public final class DailyCsvReport extends ScreeningReport {
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
