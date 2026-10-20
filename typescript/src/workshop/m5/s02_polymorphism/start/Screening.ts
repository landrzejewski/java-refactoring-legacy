import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { ScreeningRow } from '../ScreeningRow.js';

export enum Kind {
  REGULAR = 'REGULAR',
  PREMIERE = 'PREMIERE',
  MARATHON = 'MARATHON',
}

/**
 * Start: ten sam switch po rodzaju seansu w trzech metodach, a pole value znaczy raz
 * "minuty", raz "liczba filmów". Nowy rodzaj seansu = zmiana we wszystkich switchach.
 */
export class Screening {
  private constructor(
    private readonly kind: Kind,
    private readonly title: string,
    private readonly value: number,
  ) {}

  static fromRow(row: ScreeningRow): Screening {
    let kind: Kind;
    switch (row.kind) {
      case 'REGULAR': kind = Kind.REGULAR; break;
      case 'PREMIERE': kind = Kind.PREMIERE; break;
      case 'MARATHON': kind = Kind.MARATHON; break;
      default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
    }
    return new Screening(kind, row.title, row.value);
  }

  label(): string {
    switch (this.kind) {
      case Kind.REGULAR: return this.title;
      case Kind.PREMIERE: return `Premiera: ${this.title}`;
      case Kind.MARATHON: return `Maraton: ${this.title} (${this.value} filmy)`;
    }
  }

  durationMinutes(): number {
    switch (this.kind) {
      case Kind.REGULAR: return 20 + this.value;
      case Kind.PREMIERE: return 30 + this.value;
      case Kind.MARATHON: return this.value * 120 + (this.value - 1) * 15;
    }
  }

  price(): Money {
    switch (this.kind) {
      case Kind.REGULAR: return Money.of('25.00');
      case Kind.PREMIERE: return Money.of('35.00');
      case Kind.MARATHON: return Money.of('20.00').times(this.value);
    }
  }
}
