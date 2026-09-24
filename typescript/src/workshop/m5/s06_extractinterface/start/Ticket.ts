import type { Money } from '../../../shared/Money.js';

/** Start: bilet - koszyk potrzebuje z niego tylko ceny i stawki VAT, reszta API go nie obchodzi. */
export class Ticket {
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
