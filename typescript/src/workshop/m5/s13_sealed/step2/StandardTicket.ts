import type { Money } from '../../../shared/Money.js';

/** Krok 2: bilet normalny (0%). */
export class StandardTicket {
  readonly kind = 'STANDARD';
  readonly #basePrice: Money;

  constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  basePrice(): Money {
    return this.#basePrice;
  }
}
