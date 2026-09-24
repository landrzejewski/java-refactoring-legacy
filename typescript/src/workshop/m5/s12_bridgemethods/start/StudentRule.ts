import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from '../StudentTicket.js';

/** Start: reguła studencka (-25%) - też bez wspólnego typu. */
export class StudentRule {
  apply(ticket: StudentTicket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(25));
  }
}
