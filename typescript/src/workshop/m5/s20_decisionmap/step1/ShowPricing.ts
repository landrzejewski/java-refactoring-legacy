import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { DayOfWeek } from '../../../shared/time.js';

/**
 * Krok 1 (wspólny dla obu ścieżek): rozplecenie osi. Tabela 3x3 to w rzeczywistości dwie
 * niezależne reguły: cena bazowa formatu i korekta dnia. Extract Method dla każdej osi.
 */
export class ShowPricing {
  price(day: DayOfWeek, format: string): Money {
    return ShowPricing.adjustForDay(day, ShowPricing.basePrice(format));
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case '2D': return Money.of('25.00');
      case '3D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
      default: throw new IllegalArgumentError(`unknown format: ${format}`);
    }
  }

  private static adjustForDay(day: DayOfWeek, base: Money): Money {
    switch (day) {
      case 'TUESDAY': return base.minus(base.percent(30));
      case 'SATURDAY':
      case 'SUNDAY': return base.plus(Money.of('2.00'));
      default: return base;
    }
  }
}
