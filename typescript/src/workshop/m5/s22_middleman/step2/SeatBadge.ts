import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { NoSuchElementError, type ScreeningCatalog } from '../ScreeningCatalog.js';

/** Krok 2: Remove Middle Man dla pierwszego klienta - plakietka rozmawia bezpośrednio z katalogiem. */
export class SeatBadge {
  private readonly catalog: ScreeningCatalog;

  constructor(catalog: ScreeningCatalog) {
    this.catalog = requireNonNull(catalog, 'catalog');
  }

  badge(id: string): string {
    const free = this.freeSeats(id);
    if (free === 0) {
      return `${id}: WYPRZEDANE`;
    }
    return `${this.catalog.title(id)} (${this.catalog.format(id)}): ${free} wolnych`;
  }

  private freeSeats(id: string): number {
    try {
      return this.catalog.freeSeats(id);
    } catch (error) {
      if (error instanceof NoSuchElementError) {
        return 0;
      }
      throw error;
    }
  }
}
