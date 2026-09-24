import type { Hall } from './Hall.js';

/**
 * Krok 1: Self-Encapsulate Field - pole prywatne (`#vipFromRow`), WSZYSTKIE odczyty (także wewnątrz
 * klasy) idą przez getter `vipFromRow`. Teraz jest jedno miejsce, w którym zmienimy źródło wartości.
 */
export class Screening {
  readonly #vipFromRow: number;

  constructor(
    readonly hall: Hall,
    readonly format: number,
    vipFromRow: number,
  ) {
    this.#vipFromRow = vipFromRow;
  }

  get vipFromRow(): number {
    return this.#vipFromRow;
  }

  isVip(row: number): boolean {
    return row >= this.vipFromRow;
  }
}
