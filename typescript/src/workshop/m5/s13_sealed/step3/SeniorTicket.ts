import type { Money } from '../../../shared/Money.js';

/** Krok 3: bilet seniora (30%). */
export class SeniorTicket {
  readonly kind = 'SENIOR';
  readonly #basePrice: Money;

  constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  basePrice(): Money {
    return this.#basePrice;
  }
}
