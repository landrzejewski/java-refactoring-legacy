import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/** Krok 1: jedno price(Ticket) - wariant priceStudent(StudentTicket) usunięty (Safe Delete). */
export class PriceList {
  price(ticket: Ticket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(ticket.discountPercent()));
  }
}
