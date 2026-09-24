import type { Decimal } from 'decimal.js';

import { Reservation } from './Reservation.js';

/** Krok 2: serwis nie powtarza reguł modelu - tylko koordynuje. */
export class BookingService {
  book(email: string, seats: number, total: Decimal): string {
    const reservation = new Reservation(email, seats, total);
    return `zarezerwowano: ${reservation.email}, miejsc ${reservation.seats}`
      + `, kwota ${reservation.total.toFixed(2)}`;
  }
}
