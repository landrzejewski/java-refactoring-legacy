package pl.training.workshop.m3.s01_dryknowledge.step4;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m3.s01_dryknowledge.Ticket;

/**
 * Krok 4 (rozwiązanie): Extract Class - reguła ceny przeniesiona do {@link TicketPrice}.
 * BoxOffice zna już tylko własną wiedzę: reguły zwrotu. Zmiana zniżki = jedno miejsce.
 */
public final class BoxOffice {
    private static final BigDecimal REFUND_DEDUCTION = new BigDecimal("3.00");

    private final TicketPrice ticketPrice = new TicketPrice();

    public BigDecimal sell(Ticket ticket) {
        return ticketPrice.of(ticket);
    }

    public BigDecimal refund(Ticket ticket, long hoursBeforeStart) {
        BigDecimal paid = ticketPrice.of(ticket);
        int percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
        BigDecimal refund = paid.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .subtract(REFUND_DEDUCTION);
        return refund.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
