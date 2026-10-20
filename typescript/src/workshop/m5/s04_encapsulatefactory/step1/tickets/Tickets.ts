import type { Money } from '../../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import type { Ticket } from './Ticket.js';
import { VipTicket } from './VipTicket.js';

/** Krok 1: metody tworzące (Creation Method) obok klas biletów - zwracają typ Ticket. */
export class Tickets {
  private constructor() {}

  static standard(title: string, base: Money, row: number): Ticket {
    return new StandardTicket(title, base, row);
  }

  static vip(title: string, base: Money, row: number): Ticket {
    return new VipTicket(title, base, row);
  }
}
