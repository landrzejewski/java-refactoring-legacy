import type { Money } from '../../../shared/Money.js';

/**
 * Krok 1: bez zmian - każda podklasa nadal deklaruje własne price() z kolumną.
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
}
