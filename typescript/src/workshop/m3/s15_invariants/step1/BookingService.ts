import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Reservation } from './Reservation.js';

/** Krok 1: walidacja nadal tutaj, tworzenie przez konstruktor. */
export class BookingService {
  book(email: string, seats: number, total: Decimal): string {
    if (!email.includes('@')) {
      throw new IllegalArgumentError(`niepoprawny email: ${email}`);
    }
    if (seats < 1) {
      throw new IllegalArgumentError(`liczba miejsc musi byc dodatnia: ${seats}`);
    }
    if (total.lessThan(0)) {
      throw new IllegalArgumentError(`kwota nie moze byc ujemna: ${total.toFixed(2)}`);
    }
    const reservation = new Reservation(email, seats, total);
    return `zarezerwowano: ${reservation.email}, miejsc ${reservation.seats}`
      + `, kwota ${reservation.total.toFixed(2)}`;
  }
}
