import type { Money } from '../../../shared/Money.js';

/**
 * Start: baza biletów bez ceny - każda podklasa deklaruje własne price() z metadanymi kolumny "cena".
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
