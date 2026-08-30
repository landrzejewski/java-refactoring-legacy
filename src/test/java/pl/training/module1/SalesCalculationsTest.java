package pl.training.module1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

final class SalesCalculationsTest {
    @Test
    void duplicatedCalculationsCurrentlyProduceTheSameResult() {
        BigDecimal unitPrice = new BigDecimal("100.00");

        BigDecimal invoice = SalesCalculations.invoiceLineTotal(unitPrice, 1, true);
        BigDecimal quote = SalesCalculations.quoteLineTotal(unitPrice, 1, true);

        assertEquals(new BigDecimal("90.0000"), invoice);
        assertEquals(invoice, quote);
    }
}
