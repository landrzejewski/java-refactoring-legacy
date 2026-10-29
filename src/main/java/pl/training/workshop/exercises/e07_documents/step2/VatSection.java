package pl.training.workshop.exercises.e07_documents.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e07_documents.Sale;
import pl.training.workshop.shared.Money;

/** Krok 2: sekcja VAT (bilety 8%, bar 23%) dopisana pod dokumentem wewnętrznym. */
public record VatSection(Document inner, ReceiptLayout layout, Sale sale) implements Document {
    @Override
    public String render() {
        List<String> lines = new ArrayList<>();
        lines.add("VAT 8% bilet: " + vat(sale.ticketPrice(), 8));
        if (sale.barPrice().isGreaterThan(Money.ZERO)) {
            lines.add("VAT 23% bar: " + vat(sale.barPrice(), 23));
        }
        return inner.render() + "\n" + layout.section(lines);
    }

    /** VAT zawarty w cenie brutto, np. 8% z 40.00 to 2.96. */
    private static Money vat(Money gross, int rate) {
        return new Money(gross.amount()
                .multiply(BigDecimal.valueOf(rate))
                .divide(BigDecimal.valueOf(100 + rate), 2, RoundingMode.HALF_UP));
    }
}
