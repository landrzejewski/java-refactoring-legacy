package pl.training.workshop.m5.s09_overloading.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian. */
public final class PriceList {
    public Money price(Ticket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(ticket.discountPercent()));
    }
}
