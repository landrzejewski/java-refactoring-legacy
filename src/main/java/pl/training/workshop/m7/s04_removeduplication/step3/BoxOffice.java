package pl.training.workshop.m7.s04_removeduplication.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Krok 3 (rozwiązanie): kasa korzysta ze wspólnej reguły GroupDiscount. */
public final class BoxOffice {
    public BigDecimal total(List<BigDecimal> ticketPrices) {
        return GroupDiscount.ticketsTotal(ticketPrices).setScale(2, RoundingMode.HALF_UP);
    }
}
