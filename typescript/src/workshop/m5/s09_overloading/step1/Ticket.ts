import type { Money } from '../../../shared/Money.js';

/**
 * Krok 1: Replace Overloading with Overriding - zniżka to metoda instancji `discountPercent()`,
 * wybierana dynamicznie według klasy runtime obiektu. Pułapka equals(other: Ticket) jeszcze zostaje.
 */
export class Ticket {
  readonly #title: string;
  readonly #basePrice: Money;

  constructor(title: string, basePrice: Money) {
    this.#title = title;
    this.#basePrice = basePrice;
  }

  title(): string {
    return this.#title;
  }

  basePrice(): Money {
    return this.#basePrice;
  }

  discountPercent(): number {
    return 0;
  }

  equals(other: Ticket): boolean {
    return this.#title === other.#title && this.#basePrice.equals(other.#basePrice);
  }
}
