import type { TicketOrder } from '../TicketOrder.js';
import { Ticket } from './Ticket.js';

/** Start: jedyne miejsce tworzenia biletu - tu później złożymy łańcuch dekoratorów. */
export class TicketAssembler {
  assemble(order: TicketOrder): Ticket {
    return new Ticket(order);
  }
}
