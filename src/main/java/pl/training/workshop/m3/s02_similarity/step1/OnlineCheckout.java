package pl.training.workshop.m3.s02_similarity.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Krok 1: Inline Method - wspólna metoda wróciła do obu wywołujących.
 * Tymczasowe powtórzenie kodu to bezpieczny etap rozdzielania reguł.
 */
public final class OnlineCheckout {
    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = ticketPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal perUnit = ServiceFee.Kind.ONLINE_BOOKING == ServiceFee.Kind.ONLINE_BOOKING
                ? new BigDecimal("2.00") : new BigDecimal("3.00");
        return tickets.add(perUnit.multiply(BigDecimal.valueOf(ticketPrices.size()))
                .setScale(2, RoundingMode.HALF_UP));
    }
}
