import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Money } from '../../../shared/Money.js';
import type { ScreeningRow } from '../ScreeningRow.js';
import { MarathonScreening } from './MarathonScreening.js';
import { PremiereScreening } from './PremiereScreening.js';
import { RegularScreening } from './RegularScreening.js';

/** Wspólne zachowanie każdego rodzaju seansu (w Javie: metody sealed interface Screening). */
export interface ScreeningMembers {
  readonly kind: string;
  readonly title: string;

  label(): string;

  durationMinutes(): number;

  price(): Money;
}

/**
 * Krok 3: forma TypeScript - unia dyskryminowana (pole kind) zamiast sealed interface i rekordów.
 * Zestaw rodzajów jest zamknięty, więc kompilator sprawdza wyczerpujące switche także u klientów.
 */
export type Screening = RegularScreening | PremiereScreening | MarathonScreening;

export const Screening = {
  fromRow(row: ScreeningRow): Screening {
    switch (row.kind) {
      case 'REGULAR': return new RegularScreening(row.title, row.value);
      case 'PREMIERE': return new PremiereScreening(row.title, row.value);
      case 'MARATHON': return new MarathonScreening(row.title, row.value);
      default: throw new IllegalArgumentError(`unknown screening kind: ${row.kind}`);
    }
  },
};
