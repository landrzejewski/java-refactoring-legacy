import { IllegalArgumentError } from '../../../../shared/errors.js';
import type { Screening } from '../Screening.js';

/**
 * Krok 3 (rozwiązanie): Extract Method dla renderowania i Inline Variable.
 * build() opisuje algorytm na jednym poziomie abstrakcji: sprawdź, uporządkuj, wypisz.
 * Method Object nie był potrzebny - etapy przekazują sobie po jednej wartości.
 */
export class RepertoireBuilder {
  build(screenings: readonly (Screening | null)[]): string {
    const validated = this.validateAndCopy(screenings);
    const ordered = this.order(validated);
    return this.render(ordered);
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

  private order(screenings: readonly Screening[]): Screening[] {
    const active: Screening[] = [];
    for (const screening of screenings) {
      if (!screening.cancelled) {
        active.push(screening);
      }
    }
    active.sort((a, b) => a.start.compareTo(b.start) || compareText(a.title, b.title));
    return active;
  }

  private render(screenings: readonly Screening[]): string {
    let text = 'REPERTUAR\n';
    for (const screening of screenings) {
      text += `${screening.start.toString()} ${screening.title}`
        + ` (${screening.format}), sala ${screening.hall}\n`;
    }
    if (screenings.length === 0) {
      text += 'brak seansow\n';
    }
    return text;
  }
}

// Odpowiednik String.compareTo - porównanie po jednostkach kodu, niezależne od locale.
function compareText(a: string, b: string): number {
  return a < b ? -1 : a > b ? 1 : 0;
}
