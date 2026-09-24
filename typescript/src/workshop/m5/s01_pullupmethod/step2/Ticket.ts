import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Krok 2: Pull Members Up z opcją "Make abstract" dla `price()`.
 * Ciała zostają w podklasach (są różne), ale typ bazowy obiecuje cenę - to punkt rozszerzenia dla label().
 */
export abstract class Ticket {
  readonly #title: string;
  readonly #basePrice: Money;

  protected constructor(title: string, basePrice: Money) {
    this.#title = requireNonNull(title, 'title');
    this.#basePrice = requireNonNull(basePrice, 'basePrice');
  }

  title(): string {
    return this.#title;
  }

  basePrice(): Money {
    return this.#basePrice;
  }

  abstract price(): Money;
}
