import type { Money } from '../../../shared/Money.js';
import { SeniorTicket } from './SeniorTicket.js';
import { StudentTicket } from './StudentTicket.js';
import type { Ticket } from './Ticket.js';

/**
 * Start: łańcuch instanceof zakończony cichym "return 0". Nowy typ biletu (np. dziecięcy)
 * skompiluje się bez słowa i dostanie 0% zniżki.
 */
export class PriceCalculator {
  discountPercent(ticket: Ticket): number {
    if (ticket instanceof StudentTicket) {
      return 25;
    } else if (ticket instanceof SeniorTicket) {
      return 30;
    }
    return 0;
  }

  price(ticket: Ticket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(this.discountPercent(ticket)));
  }
}
