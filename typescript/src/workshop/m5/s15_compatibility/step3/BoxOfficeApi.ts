import type { Money } from '../../../shared/Money.js';
import type { Ticket } from './Ticket.js';

/**
 * Krok 3: Generalize Parameter Type - quote przyjmuje każdy Ticket. W JS "deskryptorem" metody jest
 * tylko jej nazwa, więc uogólnienie quoteStudent(StudentTicket) do quote(Ticket) usuwa starą nazwę:
 * zbudowana wtyczka dostanie TypeError "quoteStudent is not a function" (odpowiednik NoSuchMethodError).
 * W odróżnieniu od Javy build w repozytorium to wykryje (TS2339) - ale nie przebuduje cudzych wtyczek.
 */
export class BoxOfficeApi {
  quote(ticket: Ticket): Money {
    return ticket.price();
  }
}
