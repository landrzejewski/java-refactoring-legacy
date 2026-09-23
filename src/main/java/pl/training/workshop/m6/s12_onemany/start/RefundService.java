package pl.training.workshop.m6.s12_onemany.start;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/**
 * Start: osobna obsługa jednego biletu i listy biletów. Reguła zwrotu (100% / 50% / 0)
 * jest napisana dwa razy - trochę inaczej, więc łatwo o rozjazd przy następnej zmianie.
 */
public final class RefundService {
    private static final Money FEE = Money.of("3.00");

    public Money refund(TicketData ticket, LocalDateTime now) {
        Money amount;
        if (!now.isBefore(ticket.showStart())) {
            amount = Money.ZERO;
        } else if (Duration.between(now, ticket.showStart()).toHours() >= 24) {
            amount = ticket.price();
        } else {
            amount = ticket.price().percent(50);
        }
        return amount.minus(FEE).max(Money.ZERO);
    }

    public Money refundAll(List<TicketData> tickets, LocalDateTime now) {
        Money total = Money.ZERO;
        for (TicketData ticket : tickets) {
            if (now.isBefore(ticket.showStart())) {
                long hours = Duration.between(now, ticket.showStart()).toHours();
                total = total.plus(hours >= 24 ? ticket.price() : ticket.price().percent(50));
            }
        }
        return total.minus(FEE).max(Money.ZERO);
    }
}
