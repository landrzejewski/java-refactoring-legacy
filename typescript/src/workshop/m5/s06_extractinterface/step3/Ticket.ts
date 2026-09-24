import type { Money } from '../../../shared/Money.js';
import type { Priceable } from './Priceable.js';

/** Krok 3: bez zmian - implementacje nie musiały nic robić, by dostać vatAmount(). */
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
