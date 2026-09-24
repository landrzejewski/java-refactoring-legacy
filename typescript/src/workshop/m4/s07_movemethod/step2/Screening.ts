import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 2: Screening przejął też listę wolnych miejsc
 * (Move Method z BookingPrinter.remainingSeats).
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

  /**
   * Wolne miejsca bez wskazanego. Usuwamy po WARTOŚCI (`splice(indexOf(seat), 1)`), nie po pozycji:
   * "uproszczone" przy przenosinach `splice(seat, 1)` też się kompiluje (seat to number),
   * ale usuwa element o indeksie seat, a nie miejsce o tym numerze.
   */
  freeSeatsWithout(seat: number): number[] {
    const free = [...this.freeSeats];
    free.splice(free.indexOf(seat), 1);
    return free;
  }
}
