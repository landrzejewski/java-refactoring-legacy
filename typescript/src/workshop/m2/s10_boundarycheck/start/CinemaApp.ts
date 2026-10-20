import { Decimal } from 'decimal.js';

import type { LocalDateTime } from '../../../shared/time.js';
import type { ScreeningRow } from './adapter/ScreeningRow.js';
import { Screening } from './domain/Screening.js';
import { ScreeningService } from './domain/ScreeningService.js';

/**
 * Punkt startowy wariantu (composition root): składa obiekty i opisuje seans -
 * cena, wiersz bazy, zgodność odczytu. Test woła tylko tę funkcję.
 */
export function describe(title: string, start: LocalDateTime): string {
  const service = new ScreeningService(
    new Decimal('25.00'), new Decimal('5.00'), 'screenings');
  const screening = new Screening(title, start);
  const row: ScreeningRow = service.toRow(screening);
  return `${service.price(screening).toFixed(2)} | ${row.toString()} | ${service.fromRow(row).equals(screening)}`;
}
