import type { Money } from '../../../shared/Money.js';
import type { StandardTicket } from '../StandardTicket.js';
import type { PriceRule } from './PriceRule.js';

/** Krok 1: implementuje PriceRule - w czasie działania klasa wygląda tak samo jak w start (jedna metoda apply). */
export class StandardRule implements PriceRule<StandardTicket> {
  apply(ticket: StandardTicket): Money {
    return ticket.basePrice();
  }
}
