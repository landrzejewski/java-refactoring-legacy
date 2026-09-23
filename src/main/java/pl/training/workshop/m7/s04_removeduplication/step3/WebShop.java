package pl.training.workshop.m7.s04_removeduplication.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Krok 3 (rozwiązanie): sklep korzysta ze wspólnej reguły GroupDiscount; różnica
 * względem kasy (opłata 2.00 za bilet) zostaje jawna, w tej klasie.
 */
public final class WebShop {
    private static final BigDecimal FEE = new BigDecimal("2.00");

    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal fees = FEE.multiply(BigDecimal.valueOf(ticketPrices.size()));
        return GroupDiscount.ticketsTotal(ticketPrices).add(fees).setScale(2, RoundingMode.HALF_UP);
    }
}
