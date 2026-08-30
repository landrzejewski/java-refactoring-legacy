package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

final class LegacyInvoiceFormatterCharacterizationTest {
    private final LegacyInvoiceFormatter formatter = new LegacyInvoiceFormatter();

    @Test
    void documentsCurrentFormattingAndRounding() {
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("BOOK", 2, new BigDecimal("19.99")),
                new InvoiceLine("PEN", 1, new BigDecimal("5.00")));

        String result = formatter.format("  Acme  ", lines);

        assertEquals(
                String.join("\n",
                        "INVOICE",
                        "Customer: ACME",
                        "BOOK x 2 = 39.98",
                        "PEN x 1 = 5.00",
                        "Subtotal: 44.98",
                        "Tax: 10.35",
                        "Total: 55.33",
                        ""),
                result);
    }

    @Test
    void documentsCurrentFallbackForMissingCustomer() {
        String result = formatter.format(null, List.of());

        assertEquals(
                String.join("\n",
                        "INVOICE",
                        "Customer: UNKNOWN",
                        "Subtotal: 0.00",
                        "Tax: 0.00",
                        "Total: 0.00",
                        ""),
                result);
    }
}
