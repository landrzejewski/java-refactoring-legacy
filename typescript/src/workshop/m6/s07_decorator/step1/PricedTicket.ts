import type { Money } from '../../../shared/Money.js';

/** Krok 1: wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów. */
export interface PricedTicket {
  price(): Money;

  description(): string;
}
