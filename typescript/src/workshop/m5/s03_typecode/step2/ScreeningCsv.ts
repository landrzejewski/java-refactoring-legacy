import { parseInteger } from '../Integers.js';
import { Format } from './Format.js';

/** Krok 2: klient pyta obiekt formatu zamiast wykonywać switch. */
export class ScreeningCsv {
  /** Wiersz "tytuł;kod" - np. "Diuna;3". */
  describe(line: string): string {
    const parts = line.split(';');
    const title = parts[0]!;
    const format = Format.fromCode(parseInteger(parts[1]!.trim()));
    return `${title}|${format.label}|${format.basePrice.toString()}`
      + `|okulary:${format.requiresGlasses ? 'tak' : 'nie'}`
      + `|csv=${title};${format.code}`;
  }
}
