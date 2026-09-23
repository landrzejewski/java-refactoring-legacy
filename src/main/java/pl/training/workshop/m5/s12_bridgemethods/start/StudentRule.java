package pl.training.workshop.m5.s12_bridgemethods.start;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.StudentTicket;

/** Start: reguła studencka (-25%) - też bez wspólnego typu. */
public final class StudentRule {
    public Money apply(StudentTicket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(25));
    }
}
