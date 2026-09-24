import { PriceTable } from './PriceTable.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Krok 2: nowe API cennika - nazwa formatu liczona raz i przekazana do basePrice(string).
 * Zostaje ostrzeżenie fallthrough.
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
    return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
      + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
  }

  // Zapis mapy jak Map.toString() w Javie: {1=2, 10=1}.
  private static show(taken: Map<number, number>): string {
    return '{' + [...taken].map(([row, count]) => `${row}=${count}`).join(', ') + '}';
  }
}
