package pl.training.workshop.m6.s16_templatemethod.step2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m6.s16_templatemethod.Sale;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: Form Template Method - Extract Superclass + Pull Up render(). Szkielet jest final:
 * kolejność kroków i liczenie sumy należą do bazy, podklasy dostarczają tylko formatowanie.
 */
public abstract class SalesReport {
    public final String render(List<Sale> sales) {
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

    protected abstract String header();

    protected abstract String row(Sale sale);

    protected abstract String footer(int tickets, Money total);
}
