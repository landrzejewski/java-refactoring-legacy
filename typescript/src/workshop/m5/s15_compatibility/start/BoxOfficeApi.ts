import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from './StudentTicket.js';

/**
 * Start: publiczne API biblioteki kasowej, z którego korzystają ZBUDOWANE wtyczki partnerów (gotowy JS).
 * Nazwa quoteStudent - w JS metoda jest identyfikowana tylko nazwą; wariant dla biletu studenckiego
 * ma więc własną nazwę (konwencja portu: przeciążenia -> osobne nazwy).
 */
export class BoxOfficeApi {
  quoteStudent(ticket: StudentTicket): Money {
    return ticket.price();
  }
}
