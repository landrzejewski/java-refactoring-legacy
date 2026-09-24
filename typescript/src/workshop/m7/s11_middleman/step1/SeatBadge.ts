import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { NoSuchElementError } from '../ScreeningCatalog.js';
import type { CinemaFacade } from './CinemaFacade.js';

/**
 * Krok 1: plakietka sama decyduje, że nieznany seans wygląda jak wyprzedany
 * (zachowanie przeniesione z pośrednika).
 */
export class SeatBadge {
  private readonly cinema: CinemaFacade;

  constructor(cinema: CinemaFacade) {
    this.cinema = requireNonNull(cinema, 'cinema');
  }

  badge(id: string): string {
    const free = this.freeSeats(id);
    if (free === 0) {
      return `${id}: WYPRZEDANE`;
    }
    return `${this.cinema.title(id)} (${this.cinema.format(id)}): ${free} wolnych`;
  }

  private freeSeats(id: string): number {
    try {
      return this.cinema.freeSeats(id);
    } catch (error) {
      if (error instanceof NoSuchElementError) {
        return 0;
      }
      throw error;
    }
  }
}
