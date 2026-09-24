import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';
import { DayPolicies } from './DayPolicies.js';
import type { DayPolicy } from './DayPolicy.js';

/**
 * Krok 2 (ścieżka A - zmienia się reguła dnia): Replace Conditional Logic with Strategy.
 * Kalendarz polityk jest wstrzykiwany; format pozostaje prostym switchem, bo jest stabilny.
 */
export class ShowPricing {
  private readonly calendar: (day: DayOfWeek) => DayPolicy;

  constructor(calendar: (day: DayOfWeek) => DayPolicy = (day) => DayPolicies.standard(day)) {
    this.calendar = requireNonNull(calendar, 'calendar');
  }

  price(day: DayOfWeek, format: string): Money {
    return this.calendar(day)(ShowPricing.basePrice(format));
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case '2D': return Money.of('25.00');
      case '3D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
      default: throw new IllegalArgumentError(`unknown format: ${format}`);
    }
  }
}
