import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { ScreeningRow } from '../ScreeningRow.js';

/**
 * Krok 2: pozostałe gałęzie w podklasach, Screening jest abstrakcyjny, pole kind i enum Kind
 * usunięte. Jedyny switch został w miejscu tworzenia (mapowanie wiersza z bazy).
 * Podklasy są w tym samym pliku (cykl importów ESM przy "extends"); ich pliki je re-eksportują.
 */
export abstract class Screening {
  protected constructor(protected readonly title: string) {}

  static fromRow(row: ScreeningRow): Screening {
    switch (row.kind) {
      case 'REGULAR': return new RegularScreening(row.title, row.value);
      case 'PREMIERE': return new PremiereScreening(row.title, row.value);
      case 'MARATHON': return new MarathonScreening(row.title, row.value);
      default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
    }
  }

  abstract label(): string;

  abstract durationMinutes(): number;

  abstract price(): Money;
}

/** Krok 2: zwykły seans - 20 minut reklam przed filmem. */
export class RegularScreening extends Screening {
  constructor(title: string, private readonly runtime: number) {
    super(title);
  }

  override label(): string {
    return this.title;
  }

  override durationMinutes(): number {
    return 20 + this.runtime;
  }

  override price(): Money {
    return Money.of('25.00');
  }
}

/** Krok 2: premiera - 30 minut spotkania z twórcami zamiast reklam. */
export class PremiereScreening extends Screening {
  constructor(title: string, private readonly runtime: number) {
    super(title);
  }

  override label(): string {
    return `Premiera: ${this.title}`;
  }

  override durationMinutes(): number {
    return 30 + this.runtime;
  }

  override price(): Money {
    return Money.of('35.00');
  }
}

/** Krok 2: maraton - bez zmian względem kroku 1, poza konstruktorem bazy. */
export class MarathonScreening extends Screening {
  constructor(title: string, private readonly films: number) {
    super(title);
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
