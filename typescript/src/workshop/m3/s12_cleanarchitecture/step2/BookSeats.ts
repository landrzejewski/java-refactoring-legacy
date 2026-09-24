import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { BookingNotifier } from './BookingNotifier.js';
import type { BookSeatsCommand } from './BookSeatsCommand.js';
import { Booking } from './Booking.js';
import { NewReservation } from './NewReservation.js';
import type { ReservationStore } from './ReservationStore.js';

/**
 * Krok 2: przypadek użycia zależy tylko od własnych portów. Protokół efektów jest
 * jawny: najpierw zapis, potem powiadomienie - błąd zapisu oznacza brak powiadomienia.
 */
export class BookSeats {
  constructor(private readonly store: ReservationStore, private readonly notifier: BookingNotifier) {}

  execute(command: BookSeatsCommand): Booking {
    if (command.rows.length === 0) {
      throw new IllegalArgumentError('brak miejsc');
    }
    const reservation = new NewReservation(
      command.email, command.format, command.rows.length, this.price(command));
    const id = this.store.save(reservation);
    this.notifier.reservationCreated(id, reservation);
    return new Booking(id, reservation.total);
  }

  private price(command: BookSeatsCommand): Money {
    let base: Money;
    switch (command.format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    let total = Money.of('0.00');
    for (const row of command.rows) {
      total = total.plus(base);
      if (row >= 10) {
        total = total.plus(Money.of('10.00'));
      }
    }
    return total;
  }
}
