import type { TicketOrder } from '../TicketOrder.js';
import { Insurance } from './Insurance.js';
import type { PricedTicket } from './PricedTicket.js';
import { Ticket } from './Ticket.js';

/** Krok 2: fabryka składa łańcuch - rdzeń, a na zewnątrz opcjonalne ubezpieczenie. */
export class TicketAssembler {
  assemble(order: TicketOrder): PricedTicket {
    let ticket: PricedTicket = new Ticket(order);
    if (order.insurance) {
      ticket = new Insurance(ticket);
    }
    return ticket;
  }
}
