package pl.training.workshop.m5.s27_templatetostrategy.step3;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import pl.training.workshop.m5.s27_templatetostrategy.ScreeningSale;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: jedna finalna klasa złożona z dwóch strategii (Inline Class dla czterech podklas).
 * Template Method był dobry, dopóki zmieniała się jedna oś; przy dwóch niezależnych osiach
 * dziedziczenie mnoży klasy (2 x 2), a kompozycja je dodaje (2 + 2) i pozwala wybrać
 * kombinację w czasie działania. Kolejność kroków raportu nadal pilnuje jedna metoda render.
 */
public final class ScreeningReport {
    private final ReportPeriod period;
    private final ReportFormat format;

    public ScreeningReport(ReportPeriod period, ReportFormat format) {
        this.period = Objects.requireNonNull(period, "period");
        this.format = Objects.requireNonNull(format, "format");
    }

    public String render(LocalDate day, List<ScreeningSale> sales) {
        Map<String, Line> byTitle = new TreeMap<>();
        for (ScreeningSale sale : sales) {
            if (period.includes(day, sale.day())) {
                byTitle.merge(sale.title(), new Line(sale.tickets(), sale.revenue()), Line::plus);
            }
        }
        StringBuilder text = new StringBuilder(format.header(period.label(day)));
        Line total = new Line(0, Money.ZERO);
        for (Map.Entry<String, Line> entry : byTitle.entrySet()) {
            text.append(format.row(entry.getKey(), entry.getValue().tickets(), entry.getValue().revenue()));
            total = total.plus(entry.getValue());
        }
        return text.append(format.footer(total.tickets(), total.revenue())).toString();
    }

    private record Line(int tickets, Money revenue) {
        Line plus(Line other) {
            return new Line(tickets + other.tickets, revenue.plus(other.revenue));
        }
    }
}
