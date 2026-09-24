import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';
import type { Ticket } from './Ticket.js';

/** Start: klient pracuje na typie bazowym i sprawdza instanceof, zanim wywoła upgradeToVip(). */
export class BoxOffice {
  sell(kind: string, basePrice: Money, vip: boolean): Money {
    const ticket: Ticket = kind === 'STUDENT'
      ? new StudentTicket(basePrice)
      : new StandardTicket(basePrice);
    if (vip && ticket instanceof StandardTicket) {
      ticket.upgradeToVip();
    }
    return ticket.price();
  }
}
