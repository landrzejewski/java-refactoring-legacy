package pl.training.workshop.m6.s07_decorator.step2;

import pl.training.workshop.m6.s07_decorator.TicketOrder;

/** Krok 2: fabryka składa łańcuch - rdzeń, a na zewnątrz opcjonalne ubezpieczenie. */
public final class TicketAssembler {
    public PricedTicket assemble(TicketOrder order) {
        PricedTicket ticket = new Ticket(order);
        if (order.insurance()) {
            ticket = new Insurance(ticket);
        }
        return ticket;
    }
}
