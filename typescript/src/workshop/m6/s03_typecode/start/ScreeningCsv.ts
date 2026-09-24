import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { parseInteger } from '../Integers.js';

/**
 * Start: format seansu jako surowy number (1=2D, 2=3D, 3=IMAX). Wiedza o kodzie rozproszona
 * w trzech metodach, a needsGlasses w ogóle nie waliduje kodu. Plik CSV przechowuje liczbę.
 */
export class ScreeningCsv {
  static readonly FORMAT_2D = 1;
  static readonly FORMAT_3D = 2;
  static readonly FORMAT_IMAX = 3;

  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const formatCode = parseInteger(parts[1]!.trim());
    return `${title}|${this.label(formatCode)}|${this.basePrice(formatCode).toString()}`
      + `|okulary:${this.needsGlasses(formatCode) ? 'tak' : 'nie'}`
      + `|csv=${this.toCsv(title, formatCode)}`;
  }

  private label(formatCode: number): string {
    switch (formatCode) {
      case ScreeningCsv.FORMAT_2D: return '2D';
      case ScreeningCsv.FORMAT_3D: return '3D';
      case ScreeningCsv.FORMAT_IMAX: return 'IMAX';
      default: throw new IllegalArgumentError(`unknown format code: ${formatCode}`);
    }
  }

  private basePrice(formatCode: number): Money {
    switch (formatCode) {
      case ScreeningCsv.FORMAT_2D: return Money.of('25.00');
      case ScreeningCsv.FORMAT_3D: return Money.of('32.00');
      case ScreeningCsv.FORMAT_IMAX: return Money.of('40.00');
      default: throw new IllegalArgumentError(`unknown format code: ${formatCode}`);
    }
  }

  private needsGlasses(formatCode: number): boolean {
    return formatCode === ScreeningCsv.FORMAT_3D;
  }

  private toCsv(title: string, formatCode: number): string {
    return `${title};${formatCode}`;
  }
}
