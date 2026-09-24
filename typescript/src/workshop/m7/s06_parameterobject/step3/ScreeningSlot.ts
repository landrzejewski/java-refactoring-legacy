import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { LocalDate } from '../../../shared/time.js';

/**
 * Krok 3: walidacja przeniesiona do konstruktora - niepoprawny termin w ogóle nie powstaje.
 * To ZMIANA KONTRAKTU: wyjątek pojawia się wcześniej (przy tworzeniu obiektu) i także
 * dla describe(), które wcześniej niczego nie sprawdzało. Dlatego osobny krok i osobny commit.
 */
export class ScreeningSlot {
  private static readonly FORMATS: ReadonlySet<string> = new Set(['2D', '3D', 'IMAX']);

  constructor(
    readonly screeningId: string,
    readonly date: LocalDate,
    readonly hall: number,
    readonly format: string,
  ) {
    if (hall < 1 || hall > 8) {
      throw new IllegalArgumentError(`nie ma sali ${hall} (${screeningId})`);
    }
    if (!ScreeningSlot.FORMATS.has(format)) {
      throw new IllegalArgumentError(`nieznany format ${format} (${screeningId})`);
    }
  }

  label(): string {
    return `${this.screeningId} ${this.date.toString()} sala ${this.hall} (${this.format})`;
  }
}
