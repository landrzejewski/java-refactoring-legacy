package pl.training.workshop.exercises.e07_documents.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: stopka lojalnościowa - 1 punkt za każde pełne 10.00 za bilet, 100 punktów = darmowy bilet 2D. */
public record LoyaltyFooter(Document inner, ReceiptLayout layout, Sale sale) implements Document {
    @Override
    public String render() {
        int earned = sale.ticketPrice().amount().intValue() / 10;
        int total = sale.loyaltyPoints() + earned;
        List<String> lines = new ArrayList<>();
        lines.add("Punkty: " + sale.loyaltyPoints() + " + " + earned + " = " + total);
        if (total >= 100) {
            lines.add("Odbierz darmowy bilet 2D");
        }
        return inner.render() + "\n" + layout.section(lines);
    }
}
