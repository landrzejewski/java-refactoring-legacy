import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Clock } from '../../../shared/time.js';
import type { Reservation } from '../Reservation.js';
import { ReservationFactory } from './ReservationFactory.js';

/**
 * Krok 3: fabryka wstrzyknięta przez konstruktor. Test może podać fabrykę z innym zegarem,
 * a numeracja może być współdzielona przez wiele serwisów.
 */
export class ReservationService {
  private readonly factory: ReservationFactory;
  private readonly takenSeats = new Set<string>();

  /**
   * Przyjmuje fabrykę. Dotychczasowa forma z zegarem zostaje jako wygodny skrót - klienci
   * nie muszą się zmieniać (w Javie: dwa przeciążone konstruktory).
   */
  constructor(factoryOrClock: ReservationFactory | Clock) {
    requireNonNull(factoryOrClock, 'factory');
    this.factory = factoryOrClock instanceof ReservationFactory
      ? factoryOrClock
      : new ReservationFactory(factoryOrClock);
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
