import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Krok 1: nowy typ dla kodu. Zamknięty zestaw instancji wystarcza - formatów jest mało.
 * Trwały kod liczbowy jest jawnym polem, nie pozycją na liście values().
 * (W Javie: enum z polem code; TypeScriptowy enum nie ma pól, więc klasa ze stałymi instancjami.)
 */
export class Format {
  static readonly TWO_D = new Format('TWO_D', 1);
  static readonly THREE_D = new Format('THREE_D', 2);
  static readonly IMAX = new Format('IMAX', 3);

  private constructor(readonly name: 'TWO_D' | 'THREE_D' | 'IMAX', readonly code: number) {}

  static values(): readonly Format[] {
    return [Format.TWO_D, Format.THREE_D, Format.IMAX];
  }

  static fromCode(code: number): Format {
    for (const format of Format.values()) {
      if (format.code === code) {
        return format;
      }
    }
    throw new IllegalArgumentError(`unknown format code: ${code}`);
  }

  toString(): string {
    return this.name;
  }
}
