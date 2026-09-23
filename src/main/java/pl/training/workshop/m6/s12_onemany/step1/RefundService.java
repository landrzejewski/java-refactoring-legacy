package pl.training.workshop.m6.s12_onemany.step1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/** Krok 1: Extract Method ticketShare - reguła zwrotu jednego biletu w jednym miejscu. */
public final class RefundService {
    private static final Money FEE = Money.of("3.00");

    public Money refund(TicketData ticket, LocalDateTime now) {
        return ticketShare(ticket, now).minus(FEE).max(Money.ZERO);
    }

    public Money refundAll(List<TicketData> tickets, LocalDateTime now) {
        Money total = Money.ZERO;
        for (TicketData ticket : tickets) {
            total = total.plus(ticketShare(ticket, now));
        }
        return total.minus(FEE).max(Money.ZERO);
    }

    private static Money ticketShare(TicketData ticket, LocalDateTime now) {
        if (!now.isBefore(ticket.showStart())) {
            return Money.ZERO;
        }
        if (Duration.between(now, ticket.showStart()).toHours() >= 24) {
            return ticket.price();
        }
        return ticket.price().percent(50);
    }
}
