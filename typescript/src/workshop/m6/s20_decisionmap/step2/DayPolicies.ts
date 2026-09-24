import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';
import type { DayPolicy } from './DayPolicy.js';

/**
 * Krok 2 (ścieżka A): kalendarz polityk. Gdy marketing co miesiąc dodaje akcje ("środa
 * seniora", "noc kina"), zmienia się tylko ten kalendarz albo powstaje nowy.
 */
export class DayPolicies {
  static readonly CHEAP_TUESDAY: DayPolicy = (base) => base.minus(base.percent(30));
  static readonly WEEKEND: DayPolicy = (base) => base.plus(Money.of('2.00'));
  static readonly REGULAR: DayPolicy = (base) => base;

  private constructor() {}

  static standard(day: DayOfWeek): DayPolicy {
    switch (day) {
      case 'TUESDAY': return DayPolicies.CHEAP_TUESDAY;
      case 'SATURDAY':
      case 'SUNDAY': return DayPolicies.WEEKEND;
      default: return DayPolicies.REGULAR;
    }
  }
}
