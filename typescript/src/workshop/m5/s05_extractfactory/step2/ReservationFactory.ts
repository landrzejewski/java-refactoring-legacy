import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { type Clock, LocalDateTime } from '../../../shared/time.js';
import { Reservation } from '../Reservation.js';

/**
 * Krok 2: Extract Factory Class - cała wiedza o budowie rezerwacji (numeracja, opłata,
 * termin ważności) w jednej klasie. Pole sequence i zegar przeniesione razem z metodą.
 */
export class ReservationFactory {
  private sequence = 0;

  constructor(private readonly clock: Clock) {}

  create(channel: string, email: string, seats: readonly string[]): Reservation {
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
