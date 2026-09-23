package pl.training.workshop.m5.s13_sealed.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: Replace Conditional with Pattern Matching - wyczerpujący switch bez default.
 * Każdy wariant jest wymieniony jawnie, także StandardTicket (0%).
 */
public final class PriceCalculator {
    public int discountPercent(Ticket ticket) {
        return switch (ticket) {
            case StandardTicket _ -> 0;
            case StudentTicket _ -> 25;
            case SeniorTicket _ -> 30;
        };
    }

    public Money price(Ticket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(discountPercent(ticket)));
    }
}
