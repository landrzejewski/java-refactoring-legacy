package pl.training.workshop.m6.s16_templatemethod.step1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/** Krok 1: te same metody co w CSV; render() przepisany na identyczny szkielet. */
public final class HtmlSalesReport {
    public String render(List<Sale> sales) {
        List<Sale> sorted = new ArrayList<>(sales);
        sorted.sort(Comparator.comparing(Sale::time));
        StringBuilder text = new StringBuilder(header());
        int tickets = 0;
        Money total = Money.ZERO;
        for (Sale sale : sorted) {
            text.append(row(sale));
            tickets += sale.tickets();
            total = total.plus(sale.amount());
        }
        return text.append(footer(tickets, total)).toString();
    }

    private String header() {
        return "<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n";
    }

    private String row(Sale sale) {
        String title = sale.title().replace("&", "&amp;").replace("<", "&lt;");
        return "<tr><td>" + sale.time() + "</td><td>" + title + "</td><td>" + sale.tickets()
                + "</td><td>" + sale.amount() + "</td></tr>\n";
    }

    private String footer(int tickets, Money total) {
        return "<tr><td colspan=\"2\">Suma</td><td>" + tickets + "</td><td>" + total + "</td></tr>\n"
                + "</table>\n";
    }
}
