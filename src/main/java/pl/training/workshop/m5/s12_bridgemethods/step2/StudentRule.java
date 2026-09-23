package pl.training.workshop.m5.s12_bridgemethods.step2;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.StudentTicket;

/** Krok 2: reguła mówi wprost, jaki bilet obsługuje. */
public final class StudentRule implements PriceRule<StudentTicket> {
    @Override
    public Class<StudentTicket> ticketType() {
        return StudentTicket.class;
    }

    @Override
    public Money apply(StudentTicket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(25));
    }
}
