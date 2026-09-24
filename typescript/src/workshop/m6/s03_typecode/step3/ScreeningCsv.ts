import { parseInteger } from '../Integers.js';
import { FormatCodes } from './FormatCodes.js';

/** Krok 3: CSV rozmawia z mapperem, reszta kodu wyłącznie z typem Format. */
export class ScreeningCsv {
  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const format = FormatCodes.fromCode(parseInteger(parts[1]!.trim()));
    return `${title}|${format.label}|${format.basePrice.toString()}`
      + `|okulary:${format.requiresGlasses ? 'tak' : 'nie'}`
      + `|csv=${title};${FormatCodes.toCode(format)}`;
  }
}
