import { Money } from '../../../shared/Money.js';
import type { ScreeningMembers } from './Screening.js';

/** Krok 3: zwykły seans jako niezmienny obiekt wartości (w Javie: rekord). */
export class RegularScreening implements ScreeningMembers {
  readonly kind = 'REGULAR';

  constructor(readonly title: string, readonly runtime: number) {}

  label(): string {
    return this.title;
  }

  durationMinutes(): number {
    return 20 + this.runtime;
  }

  price(): Money {
    return Money.of('25.00');
  }
}
