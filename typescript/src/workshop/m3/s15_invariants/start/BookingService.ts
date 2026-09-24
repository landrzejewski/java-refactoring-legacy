import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Reservation } from './Reservation.js';

/** Start: jedyne miejsce, które waliduje - bo akurat ktoś pamiętał. */
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
    const reservation = new Reservation();
    reservation.email = email;
    reservation.seats = seats;
    reservation.total = total;
    return `zarezerwowano: ${reservation.email}, miejsc ${reservation.seats}`
      + `, kwota ${reservation.total.toFixed(2)}`;
  }
}
