import type { SingleTicket } from './SingleTicket.js';
import type { TicketGroup } from './TicketGroup.js';

/**
 * Krok 3: wspólny kontrakt "jednego" i "wielu" - kwota do zwrotu przed potrąceniem
 * (refundableAmount(now)). Zamknięta unia (odpowiednik sealed interface z Javy).
 */
export type Refundable = SingleTicket | TicketGroup;
