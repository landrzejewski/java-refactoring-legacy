import type { Money } from '../../../shared/Money.js';
import type { Priceable } from './Priceable.js';

/** Krok 1: Snack dołączony do roli Priceable - sygnatury bez zmian, tylko implements. */
export class Snack implements Priceable {
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
