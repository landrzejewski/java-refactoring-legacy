import type { Money } from '../../../../shared/Money.js';

/** Krok 3: publiczny kontrakt biletu - bez zmian. */
export interface Ticket {
  price(): Money;

  describe(): string;
}
