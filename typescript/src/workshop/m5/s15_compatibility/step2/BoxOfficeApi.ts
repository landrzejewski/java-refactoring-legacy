import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from './StudentTicket.js';

/**
 * Krok 2: bez zmian - quoteStudent(StudentTicket) woła odziedziczone price().
 */
export class BoxOfficeApi {
  quoteStudent(ticket: StudentTicket): Money {
    return ticket.price();
  }
}
