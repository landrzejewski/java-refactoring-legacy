package pl.training.workshop.m5.s15_compatibility.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bez zmian. */
public final class BoxOfficeApi {
    public Money quote(StudentTicket ticket) {
        return ticket.price();
    }
}
