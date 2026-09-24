import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';

/**
 * Start: tabela cen wpisana w zagnieżdżone warunki - format x dzień tygodnia. Dwie osie
 * zmienności (format i reguła dnia: "tani wtorek" -30%, weekend +2.00) są splecione.
 */
export class ShowPricing {
  price(day: DayOfWeek, format: string): Money {
    const weekend = day === 'SATURDAY' || day === 'SUNDAY';
    if (format === '2D') {
      return Money.of(day === 'TUESDAY' ? '17.50' : weekend ? '27.00' : '25.00');
    } else if (format === '3D') {
      return Money.of(day === 'TUESDAY' ? '22.40' : weekend ? '34.00' : '32.00');
    } else if (format === 'IMAX') {
      return Money.of(day === 'TUESDAY' ? '28.00' : weekend ? '42.00' : '40.00');
    }
    throw new IllegalArgumentError(`unknown format: ${format}`);
  }
}
