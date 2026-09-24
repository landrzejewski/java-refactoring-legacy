import type { Money } from '../../../shared/Money.js';
import type { Priceable } from './Priceable.js';

/** Krok 1: Extract Interface - Ticket implementuje Priceable (tylko price i vatPercent, nie całe API). */
export class Ticket implements Priceable {
  readonly #title: string;
  readonly #seat: string;
  readonly #price: Money;

  constructor(title: string, seat: string, price: Money) {
    this.#title = title;
    this.#seat = seat;
    this.#price = price;
  }

  title(): string {
    return this.#title;
  }

  seat(): string {
    return this.#seat;
  }

  price(): Money {
    return this.#price;
  }

  /** Bilety: VAT 8%. */
  vatPercent(): number {
    return 8;
  }
}
