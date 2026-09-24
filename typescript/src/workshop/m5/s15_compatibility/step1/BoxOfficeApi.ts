import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from './StudentTicket.js';

/**
 * Krok 1: bez zmian.
 */
export class BoxOfficeApi {
  quoteStudent(ticket: StudentTicket): Money {
    return ticket.price();
  }
}
