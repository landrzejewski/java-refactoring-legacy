import type { Screening } from './Screening.js';

/** Odpowiednik java.util.NoSuchElementException (brak we wspólnym src/shared/errors.ts). */
export class NoSuchElementError extends Error {
  override name = 'NoSuchElementError';
}

/** Stabilny kontrakt sceny: właściwy dostawca danych o seansach. Nieznany seans = wyjątek. */
export class ScreeningCatalog {
  // Map zachowuje kolejność wstawiania - jak LinkedHashMap.
  private readonly screenings = new Map<string, Screening>();

  constructor(screenings: readonly Screening[]) {
    screenings.forEach((screening) => this.screenings.set(screening.id, screening));
  }

  title(id: string): string {
    return this.find(id).title;
  }

  format(id: string): string {
    return this.find(id).format;
  }

  freeSeats(id: string): number {
    return this.find(id).freeSeats;
  }

  all(): readonly Screening[] {
    return Object.freeze([...this.screenings.values()]);
  }

  private find(id: string): Screening {
    const screening = this.screenings.get(id);
    if (screening === undefined) {
      throw new NoSuchElementError(`brak seansu ${id}`);
    }
    return screening;
  }
}
