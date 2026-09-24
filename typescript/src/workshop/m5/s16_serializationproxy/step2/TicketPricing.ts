import type { Money } from '../../../shared/Money.js';

/**
 * Krok 2: bez zmian.
 */
export class TicketPricing {
  readonly #studentDiscountPercent = 25;

  studentPrice(basePrice: Money): Money {
    return basePrice.minus(basePrice.percent(this.#studentDiscountPercent));
  }
}
