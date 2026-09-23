package pl.training.workshop.m5.s13_sealed.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: bez zmian - sealed jeszcze nic tu nie wymusza, bo łańcuch if nie jest wyczerpujący.
 */
public final class PriceCalculator {
    public int discountPercent(Ticket ticket) {
        if (ticket instanceof StudentTicket) {
            return 25;
        } else if (ticket instanceof SeniorTicket) {
            return 30;
        }
        return 0;
    }

    public Money price(Ticket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(discountPercent(ticket)));
    }
}
