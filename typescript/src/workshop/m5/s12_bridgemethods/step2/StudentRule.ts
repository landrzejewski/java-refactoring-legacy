import type { Money } from '../../../shared/Money.js';
import { StudentTicket } from '../StudentTicket.js';
import type { PriceRule, TicketType } from './PriceRule.js';

/** Krok 2: reguła mówi wprost, jaki bilet obsługuje. */
export class StudentRule implements PriceRule<StudentTicket> {
  ticketType(): TicketType<StudentTicket> {
    return StudentTicket;
  }

  apply(ticket: StudentTicket): Money {
    return ticket.basePrice().minus(ticket.basePrice().percent(25));
  }
}
