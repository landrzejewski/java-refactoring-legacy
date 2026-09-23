package pl.training.workshop.m3.s02_similarity.step3;

import java.math.BigDecimal;
import java.util.List;

/**
 * Krok 3 (rozwiązanie): Extract Constant z nazwą w języku domeny, u właściciela reguły.
 * Opłata rezerwacyjna należy do sprzedaży online i zmienia się z jej powodów
 * (promocje, konkurencja). Nie ma nic wspólnego z potrąceniem przy zwrocie.
 */
public final class OnlineCheckout {
    private static final BigDecimal BOOKING_FEE_PER_TICKET = new BigDecimal("2.00");

    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = ticketPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return tickets.add(BOOKING_FEE_PER_TICKET.multiply(BigDecimal.valueOf(ticketPrices.size())));
    }
}
