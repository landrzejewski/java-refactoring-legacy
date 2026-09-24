import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';
import type { Ticket } from './Ticket.js';
import { VipTicket } from './VipTicket.js';

/** Krok 3: klient tworzy konkretny bilet w jednym miejscu, a dalej pracuje na typie bazowym. */
export class BoxOffice {
  label(kind: string, title: string, basePrice: Money): string {
    let ticket: Ticket;
    switch (kind) {
      case 'STUDENT': ticket = new StudentTicket(title, basePrice); break;
      case 'VIP': ticket = new VipTicket(title, basePrice); break;
      default: ticket = new StandardTicket(title, basePrice);
    }
    return ticket.label();
  }
}
