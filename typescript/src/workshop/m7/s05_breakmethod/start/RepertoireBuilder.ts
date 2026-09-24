import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Screening } from '../Screening.js';

/**
 * Start: build() miesza trzy poziomy abstrakcji - kontrolę wejścia, porządkowanie
 * i renderowanie. Dane łatwo przechodzą między etapami (lista -> lista -> tekst).
 */
export class RepertoireBuilder {
  build(screenings: readonly (Screening | null)[]): string {
    const copy: Screening[] = [];
    for (const screening of screenings) {
      if (screening === null) {
        throw new IllegalArgumentError('screening must not be null');
      }
      copy.push(screening);
    }
    const active: Screening[] = [];
    for (const screening of copy) {
      if (!screening.cancelled) {
        active.push(screening);
      }
    }
    active.sort((a, b) => a.start.compareTo(b.start) || compareText(a.title, b.title));
    let text = 'REPERTUAR\n';
    for (const screening of active) {
      text += `${screening.start.toString()} ${screening.title}`
        + ` (${screening.format}), sala ${screening.hall}\n`;
    }
    if (active.length === 0) {
      text += 'brak seansow\n';
    }
    return text;
  }
}

// Odpowiednik String.compareTo - porównanie po jednostkach kodu, niezależne od locale.
function compareText(a: string, b: string): number {
  return a < b ? -1 : a > b ? 1 : 0;
}
