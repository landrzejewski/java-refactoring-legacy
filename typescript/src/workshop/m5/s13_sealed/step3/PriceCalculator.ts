import { assertNever } from '../../../../shared/assertNever.js';
import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/**
 * Krok 3 (rozwiązanie): obsługa ChildTicket (40%). Bez tej linii kompilator zgłasza TS2345:
 * Argument of type 'ChildTicket' is not assignable to parameter of type 'never'.
 */
export class PriceCalculator {
  discountPercent(ticket: Ticket): number {
    switch (ticket.kind) {
      case 'STANDARD': return 0;
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return assertNever(ticket);
    }
  }

  price(ticket: Ticket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(this.discountPercent(ticket)));
  }
}
