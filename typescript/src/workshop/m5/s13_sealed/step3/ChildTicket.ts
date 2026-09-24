import type { Money } from '../../../shared/Money.js';

/** Krok 3: NOWY typ biletu dziecięcego (40%) - kompilator wymusił jego obsługę w PriceCalculator. */
export class ChildTicket {
  readonly kind = 'CHILD';
  readonly #basePrice: Money;

  constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  basePrice(): Money {
    return this.#basePrice;
  }
}
