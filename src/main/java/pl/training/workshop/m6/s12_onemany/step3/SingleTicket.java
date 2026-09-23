package pl.training.workshop.m6.s12_onemany.step3;

import java.time.Duration;
import java.time.LocalDateTime;

import pl.training.workshop.m6.s12_onemany.TicketData;
import pl.training.workshop.shared.Money;

/** Krok 3: liść - reguła zwrotu jednego biletu (przeniesiona z ticketShare). */
public record SingleTicket(TicketData ticket) implements Refundable {
    @Override
    public Money refundableAmount(LocalDateTime now) {
        if (!now.isBefore(ticket.showStart())) {
            return Money.ZERO;
        }
        if (Duration.between(now, ticket.showStart()).toHours() >= 24) {
            return ticket.price();
        }
        return ticket.price().percent(50);
    }
}
