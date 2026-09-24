import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/** Krok 2: bez zmian. */
export class PriceList {
  price(ticket: Ticket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(ticket.discountPercent()));
  }
}
