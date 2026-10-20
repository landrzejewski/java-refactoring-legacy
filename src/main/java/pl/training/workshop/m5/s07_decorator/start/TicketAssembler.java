package pl.training.workshop.m5.s07_decorator.start;

import pl.training.workshop.m5.s07_decorator.TicketOrder;

/** Start: jedyne miejsce tworzenia biletu - tu później złożymy łańcuch dekoratorów. */
public final class TicketAssembler {
    public Ticket assemble(TicketOrder order) {
        return new Ticket(order);
    }
}
