import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { LocalDate } from '../../../shared/time.js';

/**
 * Start: data clump - screeningId, date, hall, format zawsze podróżują razem.
 * Łatwo zamienić kolejność argumentów, a walidacja siedzi tylko w ticketPrice().
 */
export class ScreeningPlanner {
  describe(screeningId: string, date: LocalDate, hall: number, format: string): string {
    return `${screeningId} ${date.toString()} sala ${hall} (${format})`;
  }

  ticketPrice(screeningId: string, _date: LocalDate, hall: number, format: string): Decimal {
    if (hall < 1 || hall > 8) {
      throw new IllegalArgumentError(`nie ma sali ${hall} (${screeningId})`);
    }
    switch (format) {
      case '2D': return new Decimal('25.00');
      case '3D': return new Decimal('32.00');
      case 'IMAX': return new Decimal('40.00');
      default: throw new IllegalArgumentError(`nieznany format ${format} (${screeningId})`);
    }
  }
}
