import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 1: Screening przejął opis seansu (Move Method z BookingPrinter.screeningLine).
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

  headline(): string {
    let formatName: string;
    switch (this.format) {
      case 3: formatName = 'IMAX'; break;
      case 2: formatName = '3D'; break;
      default: formatName = '2D';
    }
    return this.title + ' (' + formatName + '), sala ' + this.hall + ', '
      + this.start.toLocalDate().toString() + ' ' + this.start.toLocalTime().toString();
  }
}
