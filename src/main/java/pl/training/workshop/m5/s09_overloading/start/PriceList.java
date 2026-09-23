package pl.training.workshop.m5.s09_overloading.start;

import pl.training.workshop.shared.Money;

/**
 * Start: pułapka 1 - przeciążenia wybiera KOMPILATOR według typu deklarowanego argumentu.
 * Dopóki klient miał List&lt;StudentTicket&gt;, działało. Po przejściu na List&lt;Ticket&gt;
 * ten sam tekst {@code price(ticket)} wybiera price(Ticket) - student płaci pełną cenę.
 */
public final class PriceList {
    public Money price(Ticket ticket) {
        return ticket.basePrice();
    }

    public Money price(StudentTicket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(25));
    }
}
