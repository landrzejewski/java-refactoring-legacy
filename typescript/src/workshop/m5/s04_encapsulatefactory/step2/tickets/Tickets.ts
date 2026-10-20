import type { Money } from '../../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import type { Ticket } from './Ticket.js';
import { VipTicket } from './VipTicket.js';

/** Krok 2: Move Method - decyzja "który bilet" należy do fabryki, nie do klienta. */
export class Tickets {
  private static readonly VIP_FROM_ROW = 10;

  private constructor() {}

  static forSeat(title: string, base: Money, row: number): Ticket {
    if (row >= Tickets.VIP_FROM_ROW) {
      return new VipTicket(title, base, row);
    }
    return new StandardTicket(title, base, row);
  }
}
