import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { type Clock, LocalDateTime } from '../../../shared/time.js';
import { Reservation } from '../Reservation.js';

/**
 * Start: serwis pilnuje zajętości miejsc, ale też wie, jak zbudować rezerwację (numer, opłata,
 * termin ważności). Ta wiedza jest skopiowana w reserve i reserveGroup.
 */
export class ReservationService {
  private readonly takenSeats = new Set<string>();
  private sequence = 0;

  constructor(private readonly clock: Clock) {}

  reserve(channel: string, email: string, seats: readonly string[]): Reservation {
    for (const seat of seats) {
      if (this.takenSeats.has(seat)) {
        throw new IllegalStateError(`seat taken: ${seat}`);
      }
    }
    this.sequence++;
    const id = `R${this.sequence}`;
    if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
      throw new IllegalArgumentError(`unknown channel: ${channel}`);
    }
    const fee = channel === 'ONLINE' ? Money.of('2.00').times(seats.length) : Money.ZERO;
    const expiresAt = channel === 'ONLINE'
      ? LocalDateTime.now(this.clock).plusMinutes(15)
      : null;
    seats.forEach((seat) => this.takenSeats.add(seat));
    return new Reservation(id, channel, email, Object.freeze([...seats]), fee, expiresAt);
  }

  reserveGroup(email: string, seats: readonly string[]): Reservation {
    if (seats.length < 10) {
      throw new IllegalArgumentError('group needs 10+ seats');
    }
    for (const seat of seats) {
      if (this.takenSeats.has(seat)) {
        throw new IllegalStateError(`seat taken: ${seat}`);
      }
    }
    this.sequence++;
    const id = `R${this.sequence}`;
    const fee = Money.of('2.00').times(seats.length);
    const expiresAt = LocalDateTime.now(this.clock).plusMinutes(15);
    seats.forEach((seat) => this.takenSeats.add(seat));
    return new Reservation(id, 'ONLINE', email, Object.freeze([...seats]), fee, expiresAt);
  }
}
