import type { SeniorTicket } from './SeniorTicket.js';
import type { StandardTicket } from './StandardTicket.js';
import type { StudentTicket } from './StudentTicket.js';

/** Krok 2: bez zmian - zamknięta unia pozwala na switch bez default, sprawdzany przez assertNever. */
export type Ticket = StandardTicket | StudentTicket | SeniorTicket;
