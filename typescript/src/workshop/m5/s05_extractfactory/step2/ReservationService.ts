import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import type { Clock } from '../../../shared/time.js';
import type { Reservation } from '../Reservation.js';
import { ReservationFactory } from './ReservationFactory.js';

/**
 * Krok 2: serwis deleguje tworzenie do fabryki; konstruktor bez zmian, więc klienci
 * serwisu niczego nie zauważają.
 */
export class ReservationService {
  private readonly factory: ReservationFactory;
  private readonly takenSeats = new Set<string>();

  constructor(clock: Clock) {
    this.factory = new ReservationFactory(clock);
  }

  reserve(channel: string, email: string, seats: readonly string[]): Reservation {
    this.requireFree(seats);
    const reservation = this.factory.create(channel, email, seats);
    seats.forEach((seat) => this.takenSeats.add(seat));
    return reservation;
  }

  reserveGroup(email: string, seats: readonly string[]): Reservation {
    if (seats.length < 10) {
      throw new IllegalArgumentError('group needs 10+ seats');
    }
    this.requireFree(seats);
    const reservation = this.factory.create('ONLINE', email, seats);
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
}
