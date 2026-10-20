import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { type Clock, LocalDateTime } from '../../../shared/time.js';
import { Reservation } from '../Reservation.js';

/**
 * Krok 1: Extract Method - tworzenie rezerwacji w jednej metodzie newReservation.
 * Kolejność zachowana: numer jest pobierany PRZED walidacją kanału (błąd "spala" numer).
 */
export class ReservationService {
  private readonly takenSeats = new Set<string>();
  private sequence = 0;

  constructor(private readonly clock: Clock) {}

  reserve(channel: string, email: string, seats: readonly string[]): Reservation {
    this.requireFree(seats);
    const reservation = this.newReservation(channel, email, seats);
    seats.forEach((seat) => this.takenSeats.add(seat));
    return reservation;
  }

  reserveGroup(email: string, seats: readonly string[]): Reservation {
    if (seats.length < 10) {
      throw new IllegalArgumentError('group needs 10+ seats');
    }
    this.requireFree(seats);
    const reservation = this.newReservation('ONLINE', email, seats);
    seats.forEach((seat) => this.takenSeats.add(seat));
    return reservation;
  }

  private requireFree(seats: readonly string[]): void {
    for (const seat of seats) {
      if (this.takenSeats.has(seat)) {
        throw new IllegalStateError(`seat taken: ${seat}`);
      }
    }
  }

  private newReservation(channel: string, email: string, seats: readonly string[]): Reservation {
    this.sequence++;
    const id = `R${this.sequence}`;
    if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
      throw new IllegalArgumentError(`unknown channel: ${channel}`);
    }
    const fee = channel === 'ONLINE' ? Money.of('2.00').times(seats.length) : Money.ZERO;
    const expiresAt = channel === 'ONLINE'
      ? LocalDateTime.now(this.clock).plusMinutes(15)
      : null;
    return new Reservation(id, channel, email, Object.freeze([...seats]), fee, expiresAt);
  }
}
