package pl.training.workshop.m5.s09_overloading.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: jedno price(Ticket) - przeciążenie dla StudentTicket usunięte (Safe Delete). */
public final class PriceList {
    public Money price(Ticket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(ticket.discountPercent()));
    }
}
