package pl.training.workshop.m5.s27_templatetostrategy.start;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.m5.s27_templatetostrategy.ScreeningSale;
import pl.training.workshop.shared.Money;

/**
 * Start: Template Method z dwiema niezależnymi osiami w hakach - okres (które dni, jaki opis)
 * i format (CSV albo tekst). Każda kombinacja to osobna podklasa: 2 okresy x 2 formaty = 4 klasy,
 * a trzeci format dopisze kolejne dwie. Kod formatu i okresu jest skopiowany między podklasami.
 */
public abstract class ScreeningReport {
    public final String render(LocalDate day, List<ScreeningSale> sales) {
        Map<String, Line> byTitle = new TreeMap<>();
        for (ScreeningSale sale : sales) {
            if (includes(day, sale.day())) {
                byTitle.merge(sale.title(), new Line(sale.tickets(), sale.revenue()), Line::plus);
            }
        }
        StringBuilder text = new StringBuilder(header(label(day)));
        Line total = new Line(0, Money.ZERO);
        for (Map.Entry<String, Line> entry : byTitle.entrySet()) {
            text.append(row(entry.getKey(), entry.getValue().tickets(), entry.getValue().revenue()));
            total = total.plus(entry.getValue());
        }
        return text.append(footer(total.tickets(), total.revenue())).toString();
    }

    /** Hak okresu: czy sprzedaż z danego dnia należy do raportu. */
    protected abstract boolean includes(LocalDate day, LocalDate saleDay);

    /** Hak okresu: opis okresu w nagłówku. */
    protected abstract String label(LocalDate day);

    /** Hak formatu. */
    protected abstract String header(String label);

    /** Hak formatu. */
    protected abstract String row(String title, int tickets, Money revenue);

    /** Hak formatu. */
    protected abstract String footer(int tickets, Money revenue);

    private record Line(int tickets, Money revenue) {
        Line plus(Line other) {
            return new Line(tickets + other.tickets, revenue.plus(other.revenue));
        }
    }
}
