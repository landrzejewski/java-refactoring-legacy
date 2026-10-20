package pl.training.workshop.m5.s27_templatetostrategy.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian - format CSV w jednym miejscu (był skopiowany w DailyCsvReport i WeeklyCsvReport). */
public final class CsvFormat implements ReportFormat {
    @Override
    public String header(String label) {
        return "# raport " + label + "\nfilm;bilety;przychod\n";
    }

    @Override
    public String row(String title, int tickets, Money revenue) {
        String cell = title.contains(";") ? "\"" + title + "\"" : title;
        return cell + ";" + tickets + ";" + revenue + "\n";
    }

    @Override
    public String footer(int tickets, Money revenue) {
        return "SUMA;" + tickets + ";" + revenue + "\n";
    }
}
