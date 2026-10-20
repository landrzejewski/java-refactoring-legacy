import type { TicketOrder } from '../TicketOrder.js';
import { Glasses3D } from './Glasses3D.js';
import { Insurance } from './Insurance.js';
import type { PricedTicket } from './PricedTicket.js';
import { Ticket } from './Ticket.js';
import { VipSeat } from './VipSeat.js';

/**
 * Krok 3: kolejność owijania jest kontraktem - odtwarza kolejność dodatków w opisie:
 * rdzeń, VIP, okulary, ubezpieczenie.
 */
export class TicketAssembler {
  assemble(order: TicketOrder): PricedTicket {
    let ticket: PricedTicket = new Ticket(order.title, order.format, order.base);
    if (order.vip) {
      ticket = new VipSeat(ticket);
    }
    if (order.format === '3D' && !order.ownGlasses) {
      ticket = new Glasses3D(ticket);
    }
    if (order.insurance) {
      ticket = new Insurance(ticket);
    }
    return ticket;
  }
}
