import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { CinemaFacade } from './CinemaFacade.js';

/** Klient 2: tablica seansów w holu. Korzysta z pośrednika. */
export class DailyBoard {
  private readonly cinema: CinemaFacade;

  constructor(cinema: CinemaFacade) {
    this.cinema = requireNonNull(cinema, 'cinema');
  }

  render(): string {
    return this.cinema.screenings()
      .map((screening) => `${screening.id} ${screening.title} ${screening.format}`)
      .join('\n');
  }
}
