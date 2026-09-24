import type { Money } from '../../../shared/Money.js';

/** Start: przekąska z baru - te same dwie operacje co w Ticket, ale bez wspólnego typu. */
export class Snack {
  readonly #name: string;
  readonly #price: Money;

  constructor(name: string, price: Money) {
    this.#name = name;
    this.#price = price;
  }

  name(): string {
    return this.#name;
  }

  price(): Money {
    return this.#price;
  }

  /** Bar (popcorn, napoje): VAT 23%. */
  vatPercent(): number {
    return 23;
  }
}
