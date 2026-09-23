package pl.training.workshop.m5.s13_sealed.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): obsługa ChildTicket (40%). Bez tej linii kompilator zgłasza
 * "the switch expression does not cover all possible input values".
 */
public final class PriceCalculator {
    public int discountPercent(Ticket ticket) {
        return switch (ticket) {
            case StandardTicket _ -> 0;
            case StudentTicket _ -> 25;
            case SeniorTicket _ -> 30;
            case ChildTicket _ -> 40;
        };
    }

    public Money price(Ticket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(discountPercent(ticket)));
    }
}
