import { Money } from '../../../shared/Money.js';

/** Krok 3: pole #guest zniknęło z bazy (Push Down Field) - razem z gałęzią premierową describe(). */
export class Screening {
  readonly #title: string;
  readonly #format: string;
  readonly #premiere: boolean;

  protected constructor(title: string, format: string, premiere: boolean) {
    this.#title = title;
    this.#format = format;
    this.#premiere = premiere;
  }

  static regular(title: string, format: string): Screening {
    return new Screening(title, format, false);
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
    return this.#title + ' (' + this.#format + ')';
  }
}

/**
 * Krok 3: Push Down - najpierw zachowanie (override describe()), potem stan (pole #guest).
 * Gość jest wymagany w każdej premierze - null nie ma już gdzie się schować.
 */
export class PremiereScreening extends Screening {
  readonly #guest: string;

  constructor(title: string, format: string, guest: string) {
    super(title, format, true);
    this.#guest = guest;
  }

  override describe(): string {
    return super.describe() + ' - premiera, gość: ' + this.#guest;
  }
}
