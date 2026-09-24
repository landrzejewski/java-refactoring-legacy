import type { Hall } from '../Hall.js';

/**
 * Krok 2 (rozwiązanie): Substitute Algorithm - zwykłe pętle zamiast regex i tablic
 * indeksów. Złożoność istotna (co to znaczy "wolne miejsce", od którego rzędu VIP)
 * zostaje, ale jest nazwana: `FREE` i `vipRows`.
 */
export class SeatCounter {
  private static readonly FREE = '.';

  summary(hall: Hall): string {
    return `wolne: ${this.freeIn(hall.rows)}, wolne VIP: ${this.freeIn(this.vipRows(hall))}`;
  }

  private vipRows(hall: Hall): readonly string[] {
    const firstVipIndex = Math.min(Math.max(hall.vipFromRow - 1, 0), hall.rows.length);
    return hall.rows.slice(firstVipIndex);
  }

  private freeIn(rows: readonly string[]): number {
    let free = 0;
    for (const row of rows) {
      for (const seat of row) {
        if (seat === SeatCounter.FREE) {
          free++;
        }
      }
    }
    return free;
  }
}
