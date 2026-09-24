import { PriceTable } from './PriceTable.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Krok 3: wyrażenie zamiast przelotu między case - intencja "IMAX ma też Dolby"
 * zapisana wprost, bez wyciszania. Zero ostrzeżeń: bramka przechodzi.
 */
export class OccupancyReport {
  describe(map: SeatMap, format: number): string {
    const features = OccupancyReport.features(format);
    const name = format === 3 ? 'IMAX' : format === 2 ? '3D' : '2D';
    return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
      + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
  }

  private static features(format: number): string {
    switch (format) {
      case 3: return 'duzy ekran, dzwiek Dolby';
      case 2: return 'dzwiek Dolby';
      default: return 'standard';
    }
  }

  // Zapis mapy jak Map.toString() w Javie: {1=2, 10=1}.
  private static show(taken: Map<number, number>): string {
    return '{' + [...taken].map(([row, count]) => `${row}=${count}`).join(', ') + '}';
  }
}
