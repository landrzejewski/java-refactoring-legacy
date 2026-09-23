package pl.training.workshop.m6.s16_templatemethod.step2;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/** Krok 2: raport HTML to już tylko trzy metody formatujące. */
public final class HtmlSalesReport extends SalesReport {
    @Override
    protected String header() {
        return "<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n";
    }

    @Override
    protected String row(Sale sale) {
        String title = sale.title().replace("&", "&amp;").replace("<", "&lt;");
        return "<tr><td>" + sale.time() + "</td><td>" + title + "</td><td>" + sale.tickets()
                + "</td><td>" + sale.amount() + "</td></tr>\n";
    }

    @Override
    protected String footer(int tickets, Money total) {
        return "<tr><td colspan=\"2\">Suma</td><td>" + tickets + "</td><td>" + total + "</td></tr>\n"
                + "</table>\n";
    }
}
