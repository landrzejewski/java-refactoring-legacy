import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Seans - na razie sam worek danych; zachowanie o seansie mieszka w BookingPrinter.
 *
 * @param format    legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param freeSeats numery wolnych miejsc (kolejność ze starego systemu, nie zawsze rosnąca)
 */
export class Screening {
  constructor(
    readonly title: string,
    readonly format: number,
    readonly start: LocalDateTime,
    readonly hall: number,
    readonly freeSeats: readonly number[],
  ) {}
}
