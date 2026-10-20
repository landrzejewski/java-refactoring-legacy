/** Krok 2 (bez zmian): stare API (przestarzałe) i nowe API cennika. */
export class PriceTable {
  private constructor() {}

  /**
   * Cena bazowa wg kodu z CinemaManager.
   *
   * @deprecated kody number (1 = 2D, 2 = 3D, 3 = IMAX) - użyj basePrice(format: string)
   */
  static basePrice(format: number): number;
  static basePrice(format: string): number;
  static basePrice(format: number | string): number {
    if (typeof format === 'number') {
      return format === 3 ? 40 : format === 2 ? 32 : 25;
    }
    switch (format) {
      case 'IMAX': return 40;
      case '3D': return 32;
      default: return 25;
    }
  }
}
