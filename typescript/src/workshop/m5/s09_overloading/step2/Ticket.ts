import type { Money } from '../../../shared/Money.js';
import type { Equatable } from '../Equatable.js';

/**
 * Krok 2 (rozwiązanie): Ticket implementuje kontrakt Equatable - equals(other: unknown) i key()
 * (odpowiednik hashCode). Kontrakt z typem funkcyjnym zamienia zawężony parametr w błąd kompilacji.
 * Porównujemy constructor, bo bilet studencki i normalny na ten sam film to różne pozycje.
 */
export class Ticket implements Equatable {
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

  equals(other: unknown): boolean {
    return other instanceof Ticket && this.constructor === other.constructor
      && this.#title === other.#title && this.#basePrice.equals(other.#basePrice);
  }

  /** Odpowiednik hashCode(): klucz wartości do Map/Set - równe bilety mają ten sam klucz. */
  key(): string {
    return `${this.constructor.name}|${this.#title}|${this.#basePrice}`;
  }
}
