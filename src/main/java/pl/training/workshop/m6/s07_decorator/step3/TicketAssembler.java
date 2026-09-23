package pl.training.workshop.m6.s07_decorator.step3;

import pl.training.workshop.m6.s07_decorator.TicketOrder;

/**
 * Krok 3: kolejność owijania jest kontraktem - odtwarza kolejność dodatków w opisie:
 * rdzeń, VIP, okulary, ubezpieczenie.
 */
public final class TicketAssembler {
    public PricedTicket assemble(TicketOrder order) {
        PricedTicket ticket = new Ticket(order.title(), order.format(), order.base());
        if (order.vip()) {
            ticket = new VipSeat(ticket);
        }
        if (order.format().equals("3D") && !order.ownGlasses()) {
            ticket = new Glasses3D(ticket);
        }
        if (order.insurance()) {
            ticket = new Insurance(ticket);
        }
        return ticket;
    }
}
