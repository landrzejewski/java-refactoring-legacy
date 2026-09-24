import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { CinemaFacade } from './CinemaFacade.js';

/** Klient 1: plakietka z liczbą wolnych miejsc. Korzysta z pośrednika. */
export class SeatBadge {
  private readonly cinema: CinemaFacade;

  constructor(cinema: CinemaFacade) {
    this.cinema = requireNonNull(cinema, 'cinema');
  }

  badge(id: string): string {
    const free = this.cinema.freeSeats(id);
    if (free === 0) {
      return `${id}: WYPRZEDANE`;
    }
    return `${this.cinema.title(id)} (${this.cinema.format(id)}): ${free} wolnych`;
  }
}
