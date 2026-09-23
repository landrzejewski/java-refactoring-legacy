package pl.training.workshop.m6.s16_templatemethod.start;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/** Start: raport CSV - szkielet (sortowanie, nagłówek, wiersze, suma) skopiowany w raporcie HTML. */
public final class CsvSalesReport {
    public String render(List<Sale> sales) {
        List<Sale> sorted = new ArrayList<>(sales);
        sorted.sort(Comparator.comparing(Sale::time));
        StringBuilder csv = new StringBuilder("godzina;film;bilety;kwota\n");
        int tickets = 0;
        Money total = Money.ZERO;
        for (Sale sale : sorted) {
            String title = sale.title().contains(";") ? "\"" + sale.title() + "\"" : sale.title();
            csv.append(sale.time()).append(';').append(title).append(';')
                    .append(sale.tickets()).append(';').append(sale.amount()).append('\n');
            tickets += sale.tickets();
            total = total.plus(sale.amount());
        }
        csv.append("SUMA;;").append(tickets).append(';').append(total).append('\n');
        return csv.toString();
    }
}
