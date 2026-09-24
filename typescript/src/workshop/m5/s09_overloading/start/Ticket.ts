import type { Money } from '../../../shared/Money.js';

/**
 * Start: stan po Extract Superclass (wcześniej StandardTicket i StudentTicket były niezależne).
 * Pułapka 2: `equals(other: Ticket)` wygląda jak równość wartości, ale kolekcje JS go nie widzą
 * (includes porównuje tożsamość), a parametr zawężony do Ticket przeszedłby nawet przez kontrakt
 * Equatable zapisany jako metoda (biwariancja parametrów metod).
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

  equals(other: Ticket): boolean {
    return this.#title === other.#title && this.#basePrice.equals(other.#basePrice);
  }
}
