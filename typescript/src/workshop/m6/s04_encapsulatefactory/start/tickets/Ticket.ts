import type { Money } from '../../../../shared/Money.js';

/** Wspólny kontrakt biletu - jedyny typ, który klient naprawdę potrzebuje znać. */
export interface Ticket {
  price(): Money;

  describe(): string;
}
