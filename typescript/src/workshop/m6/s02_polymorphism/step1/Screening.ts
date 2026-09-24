import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { ScreeningRow } from '../ScreeningRow.js';

export enum Kind {
  REGULAR = 'REGULAR',
  PREMIERE = 'PREMIERE',
  MARATHON = 'MARATHON',
}

/**
 * Krok 1: Extract Subclass dla jednej gałęzi (MARATHON). Tworzenie w fromRow kieruje maraton
 * do podklasy, a gałęzie MARATHON w switchach stały się martwe - rzucają wyjątek.
 * Podklasa jest w tym samym pliku: w ESM cykl Screening -> MarathonScreening -> Screening
 * (extends) kończy się ReferenceError; MarathonScreening.ts ją re-eksportuje.
 */
export class Screening {
  // title widoczny dla podklas (w Javie: chronione akcesory title()).
  protected constructor(
    private readonly kind: Kind,
    protected readonly title: string,
    private readonly value: number,
  ) {}

  static fromRow(row: ScreeningRow): Screening {
    switch (row.kind) {
      case 'REGULAR': return new Screening(Kind.REGULAR, row.title, row.value);
      case 'PREMIERE': return new Screening(Kind.PREMIERE, row.title, row.value);
      case 'MARATHON': return new MarathonScreening(row.title, row.value);
      default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
    }
  }

  label(): string {
    switch (this.kind) {
      case Kind.REGULAR: return this.title;
      case Kind.PREMIERE: return `Premiera: ${this.title}`;
      case Kind.MARATHON: throw new IllegalStateError('handled by MarathonScreening');
    }
  }

  durationMinutes(): number {
    switch (this.kind) {
      case Kind.REGULAR: return 20 + this.value;
      case Kind.PREMIERE: return 30 + this.value;
      case Kind.MARATHON: throw new IllegalStateError('handled by MarathonScreening');
    }
  }

  price(): Money {
    switch (this.kind) {
      case Kind.REGULAR: return Money.of('25.00');
      case Kind.PREMIERE: return Money.of('35.00');
      case Kind.MARATHON: throw new IllegalStateError('handled by MarathonScreening');
    }
  }
}

/** Krok 1: pierwsza podklasa - maraton ma własne, nazwane dane (films zamiast value). */
export class MarathonScreening extends Screening {
  constructor(title: string, private readonly films: number) {
    super(Kind.MARATHON, title, films);
  }

  override label(): string {
    return `Maraton: ${this.title} (${this.films} filmy)`;
  }

  override durationMinutes(): number {
    return this.films * 120 + (this.films - 1) * 15;
  }

  override price(): Money {
    return Money.of('20.00').times(this.films);
  }
}
