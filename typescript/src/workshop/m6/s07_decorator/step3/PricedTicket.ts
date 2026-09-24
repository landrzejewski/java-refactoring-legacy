import type { Money } from '../../../shared/Money.js';

/** Krok 3: bez zmian - wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów. */
export interface PricedTicket {
  price(): Money;

  description(): string;
}
