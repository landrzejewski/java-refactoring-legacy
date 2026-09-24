import type { Seat } from '../Seat.js';

/**
 * Krok 2 (rozwiązanie): Return ASAP w pętli - zwracamy tam, gdzie wynik jest ostateczny.
 * Flaga found i zmienna result zniknęły. inspectedCount++ zostaje PRZED return (mutacja zachowana),
 * a length/[index] zostają - for...of zmieniłby sposób dostępu do listy (iterator).
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
    for (let index = 0; index < seats.length; index++) {
      const seat = seats[index]!;
      this.inspectedCount++;
      if (seat.row >= minRow && !seat.taken) {
        return seat.label;
      }
    }
    return undefined;
  }

  inspected(): number {
    return this.inspectedCount;
  }
}
