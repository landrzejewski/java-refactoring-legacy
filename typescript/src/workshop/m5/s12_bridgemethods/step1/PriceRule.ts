import type { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';

/**
 * Krok 1: Extract Interface - generyczna rola reguły cenowej.
 * W Javie po erasure apply(T) to apply(Ticket) i kompilator dokłada metodę bridge z rzutowaniem.
 * W TS generyk znika całkowicie i nic nie jest dokładane: PriceRule<StudentTicket> daje się
 * przypisać do PriceRule<Ticket>, bo parametry metod są sprawdzane biwariantnie - to "most" bez
 * rzutowania, więc zły bilet przejdzie po cichu.
 */
export interface PriceRule<T extends Ticket> {
  apply(ticket: T): Money;
}
