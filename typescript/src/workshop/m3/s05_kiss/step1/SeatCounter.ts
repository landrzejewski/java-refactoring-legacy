import type { Hall } from '../Hall.js';

/**
 * Krok 1: Replace Parameter with Explicit Methods - zamiast stringa "all"/"vip"
 * i wywołania dynamicznego dwie jawne metody. Kompilator i IDE znów widzą przepływ,
 * literówka nie przejdzie kompilacji.
 */
export class SeatCounter {
  private static readonly FREE = /(?<seat>\.)/g;

  summary(hall: Hall): string {
    return `wolne: ${this.freeSeats(hall)}, wolne VIP: ${this.freeVipSeats(hall)}`;
  }

  private freeSeats(hall: Hall): number {
    return this.countFree(hall.rows);
  }

  private freeVipSeats(hall: Hall): number {
    return this.countFree(Array.from({ length: hall.rows.length }, (_, i) => i + 1)
      .filter((n) => n >= hall.vipFromRow)
      .map((n) => hall.rows[n - 1]!));
  }

  private countFree(rows: Iterable<string>): number {
    return [...rows]
      .flatMap((row) => [...row.matchAll(SeatCounter.FREE)].map((m) => m.groups?.['seat']))
      .length;
  }
}
