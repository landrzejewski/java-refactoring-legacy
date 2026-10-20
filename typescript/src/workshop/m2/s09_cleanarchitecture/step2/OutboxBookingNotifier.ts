import type { Outbox } from '../Outbox.js';
import type { BookingNotifier } from './BookingNotifier.js';
import type { NewReservation } from './NewReservation.js';

/** Krok 2: adapter wyjściowy - zna temat i format komunikatu. */
export class OutboxBookingNotifier implements BookingNotifier {
  constructor(private readonly outbox: Outbox) {}

  reservationCreated(id: string, reservation: NewReservation): void {
    this.outbox.publish('reservation-created', `${id};${reservation.email};${reservation.total.toString()}`);
  }
}
