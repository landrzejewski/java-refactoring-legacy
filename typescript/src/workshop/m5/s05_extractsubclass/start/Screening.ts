import { Money } from '../../../shared/Money.js';

/**
 * Start: flaga `#premiere` i pole `#guest`, które ma sens tylko dla premier.
 * Dla zwykłego seansu guest === null, a każda metoda powtarza "if (premiere)".
 */
export class Screening {
  readonly #title: string;
  readonly #format: string;
  readonly #premiere: boolean;
  readonly #guest: string | null;

  constructor(title: string, format: string, premiere: boolean, guest: string | null) {
    this.#title = title;
    this.#format = format;
    this.#premiere = premiere;
    this.#guest = guest;
  }

  price(): Money {
    let base: Money;
    switch (this.#format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    return this.#premiere ? base.plus(Money.of('15.00')) : base;
  }

  describe(): string {
    let text = this.#title + ' (' + this.#format + ')';
    if (this.#premiere) {
      text += ' - premiera, gość: ' + this.#guest;
    }
    return text;
  }
}
