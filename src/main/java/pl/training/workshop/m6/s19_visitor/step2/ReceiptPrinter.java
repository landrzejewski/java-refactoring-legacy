package pl.training.workshop.m6.s19_visitor.step2;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: wszystkie operacje jako Visitory - żadnego instanceof. Nowa operacja = nowa klasa
 * Visitora; nowy rodzaj pozycji = zmiana interfejsu i WSZYSTKICH Visitorów.
 */
public final class ReceiptPrinter {
    private final ReceiptLineVisitor lines = new ReceiptLineVisitor();
    private final AmountVisitor amounts = new AmountVisitor();
    private final VatVisitor vats = new VatVisitor();

    public String print(List<OrderItem> items) {
        StringBuilder text = new StringBuilder();
        Money total = Money.ZERO;
        Money vat = Money.ZERO;
        for (OrderItem item : items) {
            text.append(item.accept(lines)).append('\n');
            total = total.plus(item.accept(amounts));
            vat = vat.plus(item.accept(vats));
        }
        return text.append("Razem: ").append(total.max(Money.ZERO)).append('\n')
                .append("VAT: ").append(vat).append('\n').toString();
    }
}
