import { Money } from '../../../shared/Money.js';
import type { ScreeningMembers } from './Screening.js';

/** Krok 3: maraton jako niezmienny obiekt wartości (w Javie: rekord) - pole films ma jedno znaczenie. */
export class MarathonScreening implements ScreeningMembers {
  readonly kind = 'MARATHON';

  constructor(readonly title: string, readonly films: number) {}

  label(): string {
    return `Maraton: ${this.title} (${this.films} filmy)`;
  }

  durationMinutes(): number {
    return this.films * 120 + (this.films - 1) * 15;
  }

  price(): Money {
    return Money.of('20.00').times(this.films);
  }
}
