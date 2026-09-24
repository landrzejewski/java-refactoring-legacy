import type { LocalDateTime } from '../../../shared/time.js';

/**
 * Krok 1: cennik wydzielony z CinemaManager.book(). Kod przeniesiony dosłownie
 * (łącznie z number/double i kolejnością operacji) - poprawianie typu pieniędzy to osobna decyzja (s14).
 * Eksportowany tylko na potrzeby CinemaManager (w Javie klasa pakietowa).
 */
export class PricingService {
  // format: 1 = 2D, 2 = 3D, 3 = IMAX; typ biletu: N, S, E, C

  ticketsSum(format: number, start: LocalDateTime, vipFromRow: number,
    seats: string[], types: string[], ownGlasses: boolean): number {
    let sum = 0;
    for (let i = 0; i < seats.length; i++) {
      let p = 0;
      if (format === 1) {
        p = 25.00;
      } else if (format === 2) {
        p = 32.00;
      } else if (format === 3) {
        p = 40.00;
      }
      if (types[i] === 'S') {
        p = p - p * 0.25;
      } else if (types[i] === 'E') {
        p = p - p * 0.30;
      } else if (types[i] === 'C') {
        p = p - p * 0.40;
      }
      if (start.hour < 12) {
        p = p - 5;
      }
      if (parseInt(seats[i]!.substring(1), 10) >= vipFromRow) {
        p = p + 10;
      }
      if (format === 2 && !ownGlasses) {
        p = p + 3;
      }
      sum = sum + p;
    }
    if (seats.length >= 10) {
      sum = sum - sum * 0.10;
    }
    return Math.round(sum * 100) / 100.0;
  }

  bookingFee(web: boolean, tickets: number): number {
    return web ? 2.00 * tickets : 0;
  }
}
