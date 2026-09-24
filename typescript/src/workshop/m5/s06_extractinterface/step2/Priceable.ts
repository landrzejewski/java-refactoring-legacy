import type { Money } from '../../../shared/Money.js';

/**
 * Krok 2: bez zmian.
 */
export interface Priceable {
  price(): Money;

  vatPercent(): number;
}
