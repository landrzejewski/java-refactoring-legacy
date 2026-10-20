import { IllegalArgumentError } from '../../../../../shared/errors.js';
import type { Reservation } from '../../Reservation.js';
import type { CustomerNotifier } from './CustomerNotifier.js';

/**
 * Krok 3 (rozwiązanie): Extract Interface z potrzeby + Move Method do adaptera.
 * Polityka nie importuje niczego z infra. Sterowanie nadal płynie app -> infra
 * (confirm woła notifier), ale zależność źródłowa odwróciła się: infra -> app.
 */
export class ConfirmReservation {
  constructor(private readonly notifier: CustomerNotifier) {}

  confirm(reservation: Reservation): string {
    if (reservation.seats < 1) {
      throw new IllegalArgumentError('rezerwacja bez miejsc');
    }
    const message = `Rezerwacja: ${reservation.title}, ${reservation.start.toString()}`
      + `, miejsc: ${reservation.seats}. Zaplac w ciagu 15 minut.`;
    this.notifier.notifyCustomer(reservation.email, message);
    return `potwierdzono: ${reservation.email}`;
  }
}
