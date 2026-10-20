import { Money } from '../../../shared/Money.js';
import type { ScreeningMembers } from './Screening.js';

/** Krok 3: premiera jako niezmienny obiekt wartości (w Javie: rekord). */
export class PremiereScreening implements ScreeningMembers {
  readonly kind = 'PREMIERE';

  constructor(readonly title: string, readonly runtime: number) {}

  label(): string {
    return `Premiera: ${this.title}`;
  }

  durationMinutes(): number {
    return 30 + this.runtime;
  }

  price(): Money {
    return Money.of('35.00');
  }
}
