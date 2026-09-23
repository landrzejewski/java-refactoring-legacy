package pl.training.workshop.m3.s03_falseabstraction.step2;

import java.math.BigDecimal;

/**
 * Krok 2 (rozwiązanie): karnet to osobna wiedza z osobnym właścicielem.
 * Nie wie nic o formatach, porankach ani okularach.
 */
public final class PassCounter {
    private static final BigDecimal PRICE_PER_ENTRY = new BigDecimal("20.00");

    public BigDecimal pass(int entries) {
        return PRICE_PER_ENTRY.multiply(BigDecimal.valueOf(entries));
    }
}
