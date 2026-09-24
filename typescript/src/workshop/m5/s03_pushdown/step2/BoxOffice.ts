import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';

/**
 * Krok 2: bez zmian - klient już pracuje na podtypie.
 */
export class BoxOffice {
  sell(kind: string, basePrice: Money, vip: boolean): Money {
    if (kind === 'STUDENT') {
      return new StudentTicket(basePrice).price();
    }
    const ticket: StandardTicket = new StandardTicket(basePrice);
    if (vip) {
      ticket.upgradeToVip();
    }
    return ticket.price();
  }
}
