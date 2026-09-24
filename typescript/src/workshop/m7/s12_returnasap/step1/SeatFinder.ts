import type { Seat } from '../Seat.js';

/**
 * Krok 1: guard clauses - w seatClass() przypadki kończące (brak, zajęte) zwracają od razu,
 * zmienna result zniknęła. W firstFree() guard dla null zamiast otaczającego if.
 */
export class SeatFinder {
  private inspectedCount = 0;

  seatClass(seat: Seat | null, vipFromRow: number): string {
    if (seat === null) {
      return 'BRAK';
    }
    if (seat.taken) {
      return 'ZAJETE';
    }
    if (seat.row >= vipFromRow) {
      return 'VIP';
    }
    return 'STANDARD';
  }

  firstFree(seats: readonly Seat[] | null, minRow: number): string | undefined {
    if (seats === null) {
      return undefined;
    }
    let result: string | undefined = undefined;
    let found = false;
    let index = 0;
    while (!found && index < seats.length) {
      const seat = seats[index]!;
      this.inspectedCount++;
      if (seat.row >= minRow && !seat.taken) {
        result = seat.label;
        found = true;
      }
      index++;
    }
    return result;
  }

  inspected(): number {
    return this.inspectedCount;
  }
}
