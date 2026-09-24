import { Money } from '../../../shared/Money.js';

/**
 * Krok 4 (rozwiązanie): flaga #premiere usunięta - wariant wyraża klasa runtime.
 * Baza zna tylko zwykły seans; dopłatę premierową dodaje override price() w podklasie.
 */
export class Screening {
  readonly #title: string;
  readonly #format: string;

  protected constructor(title: string, format: string) {
    this.#title = title;
    this.#format = format;
  }

  static regular(title: string, format: string): Screening {
    return new Screening(title, format);
  }

  static premiere(title: string, format: string, guest: string): Screening {
    return new PremiereScreening(title, format, guest);
  }

  price(): Money {
    switch (this.#format) {
      case 'IMAX': return Money.of('40.00');
      case '3D': return Money.of('32.00');
      default: return Money.of('25.00');
    }
  }

  describe(): string {
    return this.#title + ' (' + this.#format + ')';
  }
}

/** Krok 4: premiera = seans + gość + dopłata 15.00. Żadnego "if (premiere)". */
export class PremiereScreening extends Screening {
  static readonly #PREMIERE_SURCHARGE = Money.of('15.00');

  readonly #guest: string;

  constructor(title: string, format: string, guest: string) {
    super(title, format);
    this.#guest = guest;
  }

  override price(): Money {
    return super.price().plus(PremiereScreening.#PREMIERE_SURCHARGE);
  }

  override describe(): string {
    return super.describe() + ' - premiera, gość: ' + this.#guest;
  }
}
