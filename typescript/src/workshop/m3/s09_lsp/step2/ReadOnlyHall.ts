import { Hall } from './Hall.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Krok 2 (rozwiązanie): Replace Inheritance with Delegation. Sala archiwalna
 * implementuje tylko rolę, której kontrakt spełnia - `SeatMap` - i deleguje
 * do prywatnej kopii Hall. Metody reserve po prostu nie ma: kompilator nie pozwoli
 * przekazać sali archiwalnej do kasy.
 */
export class ReadOnlyHall implements SeatMap {
  private readonly snapshot: Hall;

  constructor(capacity: number, taken: Iterable<number>) {
    this.snapshot = new Hall(capacity);
    for (const seat of taken) {
      this.snapshot.reserve(seat);
    }
  }

  isFree(seat: number): boolean {
    return this.snapshot.isFree(seat);
  }

  freeSeats(): number {
    return this.snapshot.freeSeats();
  }

  capacity(): number {
    return this.snapshot.capacity();
  }
}
