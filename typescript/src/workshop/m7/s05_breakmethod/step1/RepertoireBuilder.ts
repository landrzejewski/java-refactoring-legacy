import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Screening } from '../Screening.js';

/**
 * Krok 1: Extract Method dla fragmentu z jednym wejściem i jednym wynikiem -
 * kontrola wejścia i kopia przeniesione dosłownie do validateAndCopy().
 */
export class RepertoireBuilder {
  build(screenings: readonly (Screening | null)[]): string {
    const copy = this.validateAndCopy(screenings);
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

  private validateAndCopy(screenings: readonly (Screening | null)[]): Screening[] {
    const copy: Screening[] = [];
    for (const screening of screenings) {
      if (screening === null) {
        throw new IllegalArgumentError('screening must not be null');
      }
      copy.push(screening);
    }
    return copy;
  }
}

// Odpowiednik String.compareTo - porównanie po jednostkach kodu, niezależne od locale.
function compareText(a: string, b: string): number {
  return a < b ? -1 : a > b ? 1 : 0;
}
