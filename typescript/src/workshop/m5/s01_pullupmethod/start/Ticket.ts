import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Start: wspólna nadklasa biletów istnieje, ale nie wie nic o cenie ani etykiecie.
 * Każda podklasa ma własne `price()` i własne `label()` - trzy teksty, jedno zachowanie.
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
}
