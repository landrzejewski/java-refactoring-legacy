package pl.training.workshop.m6.s07_decorator.step1;

import pl.training.workshop.m6.s07_decorator.TicketOrder;

/** Krok 1: fabryka zwraca interfejs, więc może później zwrócić udekorowany obiekt. */
public final class TicketAssembler {
    public PricedTicket assemble(TicketOrder order) {
        return new Ticket(order);
    }
}
