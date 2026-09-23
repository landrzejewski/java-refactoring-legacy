package pl.training.workshop.m5.s15_compatibility.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian - quote(StudentTicket) woła odziedziczone price(). */
public final class BoxOfficeApi {
    public Money quote(StudentTicket ticket) {
        return ticket.price();
    }
}
