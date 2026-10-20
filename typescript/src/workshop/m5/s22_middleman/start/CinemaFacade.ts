import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Screening } from '../Screening.js';
import { NoSuchElementError, type ScreeningCatalog } from '../ScreeningCatalog.js';

/**
 * Start: pośrednik - prawie każda metoda deleguje 1:1 do ScreeningCatalog.
 * Prawie: freeSeats() po cichu tłumaczy "brak seansu" na 0. Zanim usuniesz pośrednika,
 * sprawdź, co naprawdę robi (autoryzacja, logi, transakcje, translacja błędów).
 */
export class CinemaFacade {
  private readonly catalog: ScreeningCatalog;

  constructor(catalog: ScreeningCatalog) {
    this.catalog = requireNonNull(catalog, 'catalog');
  }

  title(id: string): string {
    return this.catalog.title(id);
  }

  format(id: string): string {
    return this.catalog.format(id);
  }

  freeSeats(id: string): number {
    try {
      return this.catalog.freeSeats(id);
    } catch (error) {
      if (error instanceof NoSuchElementError) {
        return 0;
      }
      throw error;
    }
  }

  screenings(): readonly Screening[] {
    return this.catalog.all();
  }
}
