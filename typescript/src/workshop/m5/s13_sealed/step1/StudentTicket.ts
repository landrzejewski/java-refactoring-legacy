import type { Money } from '../../../shared/Money.js';

/** Krok 1: bilet studencki (25%). */
export class StudentTicket {
  readonly kind = 'STUDENT';
  readonly #basePrice: Money;

  constructor(basePrice: Money) {
    this.#basePrice = basePrice;
  }

  basePrice(): Money {
    return this.#basePrice;
  }
}
