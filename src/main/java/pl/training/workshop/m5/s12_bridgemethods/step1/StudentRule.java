package pl.training.workshop.m5.s12_bridgemethods.step1;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.StudentTicket;

/** Krok 1: implementuje PriceRule - w pliku .class są teraz DWIE metody apply (jedna to bridge). */
public final class StudentRule implements PriceRule<StudentTicket> {
    @Override
    public Money apply(StudentTicket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(25));
    }
}
