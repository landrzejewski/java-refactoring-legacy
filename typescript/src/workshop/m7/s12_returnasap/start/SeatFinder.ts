import type { Seat } from '../Seat.js';

/**
 * Start: "jeden punkt wyjścia" - wynik niesiony w zmiennej result i fladze found,
 * zagnieżdżone if-y. Licznik inspected (metryka dla działu IT) jest efektem ubocznym,
 * który musi przetrwać każdą zmianę.
 */
export class SeatFinder {
  private inspectedCount = 0;

  seatClass(seat: Seat | null, vipFromRow: number): string {
    let result = 'STANDARD';
    if (seat !== null) {
      if (!seat.taken) {
        if (seat.row >= vipFromRow) {
          result = 'VIP';
        }
      } else {
        result = 'ZAJETE';
      }
    } else {
      result = 'BRAK';
    }
    return result;
  }

  firstFree(seats: readonly Seat[] | null, minRow: number): string | undefined {
    let result: string | undefined = undefined;
    if (seats !== null) {
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
    }
    return result;
  }

  inspected(): number {
    return this.inspectedCount;
  }
}
