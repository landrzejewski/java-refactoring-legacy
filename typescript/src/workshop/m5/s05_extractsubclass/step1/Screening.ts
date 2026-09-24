import { Money } from '../../../shared/Money.js';

/**
 * Krok 1: Replace Constructor with Factory Method - regular(...) i premiere(...).
 * Punkty tworzenia są teraz w jednym miejscu; to one za chwilę wybiorą klasę runtime.
 */
export class Screening {
  readonly #title: string;
  readonly #format: string;
  readonly #premiere: boolean;
  readonly #guest: string | null;

  private constructor(title: string, format: string, premiere: boolean, guest: string | null) {
    this.#title = title;
    this.#format = format;
    this.#premiere = premiere;
    this.#guest = guest;
  }

  static regular(title: string, format: string): Screening {
    return new Screening(title, format, false, null);
  }

  static premiere(title: string, format: string, guest: string): Screening {
    return new Screening(title, format, true, guest);
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
