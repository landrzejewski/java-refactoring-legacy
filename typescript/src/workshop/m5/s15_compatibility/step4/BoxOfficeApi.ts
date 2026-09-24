import type { Money } from '../../../shared/Money.js';
import type { StudentTicket } from './StudentTicket.js';
import type { Ticket } from './Ticket.js';

/**
 * Krok 4 (rozwiązanie): stara nazwa wraca jako przestarzała metoda delegująca.
 * Zbudowane wtyczki znajdują swoją metodę, nowi klienci widzą w IDE przekreślenie (@deprecated).
 * Usunięcie planujemy jako osobną, zapowiedzianą zmianę łamiącą (np. w wersji 3.0).
 */
export class BoxOfficeApi {
  quote(ticket: Ticket): Money {
    return ticket.price();
  }

  /**
   * Zgodność ze zbudowanymi wtyczkami 1.x.
   * @deprecated od 2.0 - użyj quote(ticket).
   */
  quoteStudent(ticket: StudentTicket): Money {
    return this.quote(ticket);
  }
}
