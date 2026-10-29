package pl.training.workshop.exercises.e07_documents.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/** Krok 2: paragon kiosku - kwoty wyrównane do prawej w 32 kolumnach. */
final class KioskReceiptLayout implements ReceiptLayout {
    @Override
    public String receipt(Sale sale) {
        List<String> lines = new ArrayList<>();
        lines.add("PARAGON");
        lines.add(row("Bilet", sale.ticketPrice()));
        if (sale.barPrice().isGreaterThan(Money.ZERO)) {
            lines.add(row("Bar", sale.barPrice()));
        }
        lines.add(row("RAZEM", sale.ticketPrice().plus(sale.barPrice())));
        lines.add(KioskTicketLayout.RULE);
        return String.join("\n", lines);
    }

    @Override
    public String section(List<String> lines) {
        return String.join("\n", lines) + "\n" + "-".repeat(32);
    }

    private static String row(String label, Money amount) {
        return String.format("%-22s%10s", label, amount);
    }
}
