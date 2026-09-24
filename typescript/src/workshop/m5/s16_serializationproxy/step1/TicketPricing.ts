import type { Money } from '../../../shared/Money.js';

/**
 * Krok 1: bez zmian - nadal pole # i bez interfejsu (tym zajmiemy się w kroku 3).
 */
export class TicketPricing {
  readonly #studentDiscountPercent = 25;

  studentPrice(basePrice: Money): Money {
    return basePrice.minus(basePrice.percent(this.#studentDiscountPercent));
  }
}
