import type { Hall } from './Hall.js';

/** Klient odczytu: raport obłożenia - potrzebuje tylko planu miejsc. */
export class OccupancyReport {
  describe(hall: Hall): string {
    return `zajete ${hall.capacity() - hall.freeSeats()} z ${hall.capacity()}`;
  }
}
