package pl.training.workshop.exercises.e07_documents.step1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/** Krok 1: paragon fiskalny A4 z wykropkowaniem do kwoty. */
final class BoxOfficeReceiptLayout implements ReceiptLayout {
    @Override
    public String receipt(Sale sale) {
        List<String> lines = new ArrayList<>();
        lines.add("PARAGON FISKALNY");
        lines.add(row("Bilet", sale.ticketPrice()));
        if (sale.barPrice().isGreaterThan(Money.ZERO)) {
            lines.add(row("Bar", sale.barPrice()));
        }
        lines.add(row("RAZEM", sale.ticketPrice().plus(sale.barPrice())));
        return String.join("\n", lines);
    }

    @Override
    public String section(List<String> lines) {
        return lines.stream().map(line -> "  " + line).collect(Collectors.joining("\n"));
    }

    private static String row(String label, Money amount) {
        return label + " " + ".".repeat(18 - label.length()) + " " + amount;
    }
}
