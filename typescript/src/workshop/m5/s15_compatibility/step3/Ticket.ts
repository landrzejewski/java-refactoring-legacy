import type { Money } from '../../../shared/Money.js';
import { column } from '../Column.js';

/**
 * Krok 3: bez zmian.
 */
export abstract class Ticket {
  readonly #title: string;
  readonly #basePrice: Money;

  protected constructor(title: string, basePrice: Money) {
    this.#title = title;
    this.#basePrice = basePrice;
  }

  title(): string {
    return this.#title;
  }

  basePrice(): Money {
    return this.#basePrice;
  }

  static {
    column(this.prototype.price, 'cena');
  }

  price(): Money {
    return this.#basePrice.minus(this.#basePrice.percent(this.discountPercent()));
  }

  protected abstract discountPercent(): number;
}
