import type { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';

/** Klasa biletu jako wartość w czasie działania - odpowiednik Class<T>. */
export type TicketType<T extends Ticket> = abstract new (...args: never[]) => T;

/**
 * Krok 2 (rozwiązanie): reguła jawnie deklaruje obsługiwany typ - `ticketType()` zastępuje
 * zgadywanie z nazw (Replace Reflection with Explicit Contract).
 */
export interface PriceRule<T extends Ticket> {
  ticketType(): TicketType<T>;

  apply(ticket: T): Money;
}
