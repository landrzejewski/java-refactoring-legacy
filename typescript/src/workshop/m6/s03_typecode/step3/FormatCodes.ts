import { IllegalArgumentError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Format } from './Format.js';

/**
 * Krok 3: mapper migracyjny liczba <-> Format. Baza i CSV zostają przy kodzie liczbowym;
 * gdy kiedyś przejdziemy na kody tekstowe, zmieni się tylko ta klasa.
 */
export class FormatCodes {
  private static readonly CODES: ReadonlyMap<Format, number> = new Map([
    [Format.TWO_D, 1],
    [Format.THREE_D, 2],
    [Format.IMAX, 3],
  ]);

  private constructor() {}

  static fromCode(code: number): Format {
    for (const [format, formatCode] of FormatCodes.CODES) {
      if (formatCode === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format code: ${code}`);
  }

  static toCode(format: Format): number {
    return requireNonNull(FormatCodes.CODES.get(format));
  }
}
