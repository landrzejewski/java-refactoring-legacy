package pl.training.workshop.m5.s27_templatetostrategy.step1;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import pl.training.workshop.m5.s27_templatetostrategy.ScreeningSale;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Replace Inheritance with Delegation dla osi "format". Szkielet deleguje formatowanie
 * do ReportFormat podanego w konstruktorze; w hierarchii zostały tylko haki okresu.
 */
public abstract class ScreeningReport {
    private final ReportFormat format;

    protected ScreeningReport(ReportFormat format) {
        this.format = Objects.requireNonNull(format, "format");
    }

    public final String render(LocalDate day, List<ScreeningSale> sales) {
        Map<String, Line> byTitle = new TreeMap<>();
        for (ScreeningSale sale : sales) {
            if (includes(day, sale.day())) {
                byTitle.merge(sale.title(), new Line(sale.tickets(), sale.revenue()), Line::plus);
            }
        }
        StringBuilder text = new StringBuilder(format.header(label(day)));
        Line total = new Line(0, Money.ZERO);
        for (Map.Entry<String, Line> entry : byTitle.entrySet()) {
            text.append(format.row(entry.getKey(), entry.getValue().tickets(), entry.getValue().revenue()));
            total = total.plus(entry.getValue());
        }
        return text.append(format.footer(total.tickets(), total.revenue())).toString();
    }

    /** Hak okresu: czy sprzedaż z danego dnia należy do raportu. */
    protected abstract boolean includes(LocalDate day, LocalDate saleDay);

    /** Hak okresu: opis okresu w nagłówku. */
    protected abstract String label(LocalDate day);

    private record Line(int tickets, Money revenue) {
        Line plus(Line other) {
            return new Line(tickets + other.tickets, revenue.plus(other.revenue));
        }
    }
}
