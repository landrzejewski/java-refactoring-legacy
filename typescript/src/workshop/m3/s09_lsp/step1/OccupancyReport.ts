import type { SeatMap } from './SeatMap.js';

/** Krok 1: raport zależy od roli SeatMap ("Use Interface Where Possible"), nie od Hall. */
export class OccupancyReport {
  describe(seats: SeatMap): string {
    return `zajete ${seats.capacity() - seats.freeSeats()} z ${seats.capacity()}`;
  }
}
