import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';

/**
 * Krok 3 (rozwiązanie): Pull Members Up dla `label()` - trzy identyczne ciała stały się jedną metodą.
 * W Javie `final`, bo etykieta jest kontraktem wspólnym dla wszystkich biletów; wariantem jest tylko cena.
 * TypeScript nie ma metod `final` - regułę "podklasa nie deklaruje label()" pilnuje S01SolutionTest.
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

  label(): string {
    return this.title() + ': ' + this.price();
  }
}
