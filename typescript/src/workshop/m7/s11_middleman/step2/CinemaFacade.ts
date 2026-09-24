import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Screening } from '../Screening.js';
import { type ScreeningCatalog } from '../ScreeningCatalog.js';

/**
 * Krok 2: pośrednik (czysty forwarder od kroku 1) został już tylko jednemu klientowi - DailyBoard.
 * Usuniemy go, gdy zmigrujemy ostatniego klienta.
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
    return this.catalog.freeSeats(id);
  }

  screenings(): readonly Screening[] {
    return this.catalog.all();
  }
}
