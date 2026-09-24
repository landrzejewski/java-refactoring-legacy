import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ScreeningCatalog } from '../ScreeningCatalog.js';

/**
 * Krok 3 (rozwiązanie): ostatni klient zmigrowany, CinemaFacade usunięta (Safe Delete).
 * Odwrotny ruch to Hide Delegate - wrócimy do niego, gdy pośrednik zacznie coś wnosić.
 */
export class DailyBoard {
  private readonly catalog: ScreeningCatalog;

  constructor(catalog: ScreeningCatalog) {
    this.catalog = requireNonNull(catalog, 'catalog');
  }

  render(): string {
    return this.catalog.all()
      .map((screening) => `${screening.id} ${screening.title} ${screening.format}`)
      .join('\n');
  }
}
