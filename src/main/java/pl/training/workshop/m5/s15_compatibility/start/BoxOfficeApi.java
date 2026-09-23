package pl.training.workshop.m5.s15_compatibility.start;

import pl.training.workshop.shared.Money;

/** Start: publiczne API biblioteki kasowej, z którego korzystają SKOMPILOWANE wtyczki partnerów. */
public final class BoxOfficeApi {
    public Money quote(StudentTicket ticket) {
        return ticket.price();
    }
}
