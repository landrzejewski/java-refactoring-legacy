import { PriceTable } from './PriceTable.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Krok 1 (bez zmian): raport obłożenia. Nadal woła przestarzałe PriceTable.basePrice(number) (deprecation)
 * i celowo "przelatuje" z IMAX do 3D w switch (fallthrough) - IMAX ma też dźwięk Dolby.
 */
export class OccupancyReport {
  describe(map: SeatMap, format: number): string {
    let features = '';
    switch (format) {
      // @ts-expect-error TS7029 - celowy przelot: IMAX ma też dźwięk Dolby
      case 3:
        features = features + 'duzy ekran, ';
      case 2:
        features = features + 'dzwiek Dolby';
        break;
      default:
        features = 'standard';
    }
    const name = format === 3 ? 'IMAX' : format === 2 ? '3D' : '2D';
    return name + ' [' + features + '], cena ' + PriceTable.basePrice(format)
      + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
  }

  // Zapis mapy jak Map.toString() w Javie: {1=2, 10=1}.
  private static show(taken: Map<number, number>): string {
    return '{' + [...taken].map(([row, count]) => `${row}=${count}`).join(', ') + '}';
  }
}
