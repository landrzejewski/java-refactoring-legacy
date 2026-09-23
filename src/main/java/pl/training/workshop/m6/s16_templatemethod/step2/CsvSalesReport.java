package pl.training.workshop.m6.s16_templatemethod.step2;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/** Krok 2: raport CSV to już tylko trzy metody formatujące. */
public final class CsvSalesReport extends SalesReport {
    @Override
    protected String header() {
        return "godzina;film;bilety;kwota\n";
    }

    @Override
    protected String row(Sale sale) {
        String title = sale.title().contains(";") ? "\"" + sale.title() + "\"" : sale.title();
        return sale.time() + ";" + title + ";" + sale.tickets() + ";" + sale.amount() + "\n";
    }

    @Override
    protected String footer(int tickets, Money total) {
        return "SUMA;;" + tickets + ";" + total + "\n";
    }
}
