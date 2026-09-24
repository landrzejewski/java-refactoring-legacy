import { Money } from '../../../shared/Money.js';

/**
 * Krok 2: Extract Subclass - fabryka premiere(...) tworzy PremiereScreening.
 * Hierarchia zamknięta w tym module (odpowiednik sealed permits PremiereScreening), konstruktor protected.
 * Logika jeszcze bez zmian.
 */
export class Screening {
  readonly #title: string;
  readonly #format: string;
  readonly #premiere: boolean;
  readonly #guest: string | null;

  protected constructor(title: string, format: string, premiere: boolean, guest: string | null) {
    this.#title = title;
    this.#format = format;
    this.#premiere = premiere;
    this.#guest = guest;
  }

  static regular(title: string, format: string): Screening {
    return new Screening(title, format, false, null);
  }

  static premiere(title: string, format: string, guest: string): Screening {
    return new PremiereScreening(title, format, guest);
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

/**
 * Krok 2: nowa podklasa - na razie pusta, tylko przekazuje flagę i gościa do bazy.
 * W tym samym module co Screening: fabryka bazy tworzy podklasę, a osobny plik dałby cykl importów
 * (podklasa potrzebuje bazy już przy ładowaniu modułu).
 */
export class PremiereScreening extends Screening {
  constructor(title: string, format: string, guest: string) {
    super(title, format, true, guest);
  }
}
