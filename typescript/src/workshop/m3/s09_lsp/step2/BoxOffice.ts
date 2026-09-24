import type { Hall } from './Hall.js';

/** Klient zapisu: kasa ufa kontraktowi Hall.reserve. */
export class BoxOffice {
  sell(hall: Hall, seat: number): string {
    hall.reserve(seat);
    return `sprzedano miejsce ${seat}, wolnych: ${hall.freeSeats()}`;
  }
}
