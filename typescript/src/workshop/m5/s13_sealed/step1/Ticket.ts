import type { SeniorTicket } from './SeniorTicket.js';
import type { StandardTicket } from './StandardTicket.js';
import type { StudentTicket } from './StudentTicket.js';

/**
 * Krok 1: odpowiednik sealed permits - unia dyskryminowana polem `kind`, zamknięta lista wariantów
 * w jednym miejscu. Warianty mają pola prywatne (#), więc są nominalne: obcy obiekt o tym samym
 * kształcie nie jest już biletem.
 */
export type Ticket = StandardTicket | StudentTicket | SeniorTicket;
