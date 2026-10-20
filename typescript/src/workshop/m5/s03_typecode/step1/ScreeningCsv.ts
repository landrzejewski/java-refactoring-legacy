import { Money } from '../../../shared/Money.js';
import { parseInteger } from '../Integers.js';
import { Format } from './Format.js';

/**
 * Krok 1: Replace Type Code with Class - liczba zamieniana na Format na granicy (odczyt CSV),
 * metody przyjmują Format. Do CSV nadal trafia ta sama liczba (format.code).
 */
export class ScreeningCsv {
  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const format = Format.fromCode(parseInteger(parts[1]!.trim()));
    return `${title}|${this.label(format)}|${this.basePrice(format).toString()}`
      + `|okulary:${this.needsGlasses(format) ? 'tak' : 'nie'}`
      + `|csv=${this.toCsv(title, format)}`;
  }

  // switch po format.name jest wyczerpujący - kompilator pilnuje wszystkich formatów.
  private label(format: Format): string {
    switch (format.name) {
      case 'TWO_D': return '2D';
      case 'THREE_D': return '3D';
      case 'IMAX': return 'IMAX';
    }
  }

  private basePrice(format: Format): Money {
    switch (format.name) {
      case 'TWO_D': return Money.of('25.00');
      case 'THREE_D': return Money.of('32.00');
      case 'IMAX': return Money.of('40.00');
    }
  }

  private needsGlasses(format: Format): boolean {
    return format === Format.THREE_D;
  }

  private toCsv(title: string, format: Format): string {
    return `${title};${format.code}`;
  }
}
