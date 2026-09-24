import type { Money } from '../../../shared/Money.js';
import { column } from '../Column.js';

/**
 * Krok 2: Pull Members Up - price() (z metadanymi kolumny) w bazie, różnica w haku discountPercent().
 * Klasa deklarująca metodę zmieniła się na Ticket: stare wywołania studentTicket.price() dalej działają
 * (JS szuka metody w łańcuchu prototypów), ale prototyp podklasy już jej nie ma.
 * (W Javie price() jest final - TS nie ma metod final.)
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
