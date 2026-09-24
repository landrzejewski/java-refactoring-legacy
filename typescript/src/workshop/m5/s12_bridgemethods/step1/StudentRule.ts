import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from '../StudentTicket.js';
import type { PriceRule } from './PriceRule.js';

/** Krok 1: implementuje PriceRule - w czasie działania klasa wygląda tak samo jak w start (jedna metoda apply). */
export class StudentRule implements PriceRule<StudentTicket> {
  apply(ticket: StudentTicket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(25));
  }
}
