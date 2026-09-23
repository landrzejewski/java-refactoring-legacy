package pl.training.workshop.m6.s16_templatemethod.step1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Method na różnicach (header, row, footer) - w obu raportach te same nazwy
 * i sygnatury, więc render() obu klas staje się identyczny.
 */
public final class CsvSalesReport {
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
        return "godzina;film;bilety;kwota\n";
    }

    private String row(Sale sale) {
        String title = sale.title().contains(";") ? "\"" + sale.title() + "\"" : sale.title();
        return sale.time() + ";" + title + ";" + sale.tickets() + ";" + sale.amount() + "\n";
    }

    private String footer(int tickets, Money total) {
        return "SUMA;;" + tickets + ";" + total + "\n";
    }
}
