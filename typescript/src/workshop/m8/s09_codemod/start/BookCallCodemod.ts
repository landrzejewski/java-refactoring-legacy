import type { ProjectFile } from '../SampleProject.js';

/**
 * Start: migracja "ręczna z grepem". Lista miejsc powstaje z wyrażenia regularnego, a przepisanie
 * to zamiana tekstu dla czterech kombinacji literałów. Regex nie zna komentarzy, wywołań
 * rozbitych na kilka linii ani typu odbiorcy (HotelService też ma book z dwoma boolean).
 */
export class BookCallCodemod {
  private static readonly OLD_CALL = /\bbook\(.*,\s*(true|false)\s*,\s*\w+\s*\)/;

  /** Źródła projektu (API) - wersja regexowa ich nie potrzebuje. */
  constructor(_projectSources: readonly ProjectFile[]) {}

  findLines(source: string): number[] {
    const lines: number[] = [];
    const text = source.split('\n');
    for (let i = 0; i < text.length; i++) {
      if (BookCallCodemod.OLD_CALL.test(text[i]!)) {
        lines.push(i + 1);
      }
    }
    return lines;
  }

  rewrite(source: string): string {
    return source
      .replace(', true, true)', ', Channel.WEB, Glasses.OWN)')
      .replace(', true, false)', ', Channel.WEB, Glasses.RENTED)')
      .replace(', false, true)', ', Channel.BOX_OFFICE, Glasses.OWN)')
      .replace(', false, false)', ', Channel.BOX_OFFICE, Glasses.RENTED)')
      .replace("import { BookingService } from '../cinema/BookingService.js';",
        "import { BookingService } from '../cinema/BookingService.js';\n"
        + "import { Channel } from '../cinema/Channel.js';\n"
        + "import { Glasses } from '../cinema/Glasses.js';");
  }
}
