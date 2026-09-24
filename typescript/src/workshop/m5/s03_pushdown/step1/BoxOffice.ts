import type { Money } from '../../../shared/Money.js';
import { StandardTicket } from './StandardTicket.js';
import { StudentTicket } from './StudentTicket.js';

/**
 * Krok 1: klienci na podtyp - upgradeToVip() wołamy na zmiennej typu StandardTicket.
 * Po tym kroku nikt nie woła metody przez typ bazowy, więc można ją przesunąć w dół.
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
