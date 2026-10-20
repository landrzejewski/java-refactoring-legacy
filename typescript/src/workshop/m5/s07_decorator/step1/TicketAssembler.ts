import type { TicketOrder } from '../TicketOrder.js';
import type { PricedTicket } from './PricedTicket.js';
import { Ticket } from './Ticket.js';

/** Krok 1: fabryka zwraca interfejs, więc może później zwrócić udekorowany obiekt. */
export class TicketAssembler {
  assemble(order: TicketOrder): PricedTicket {
    return new Ticket(order);
  }
}
