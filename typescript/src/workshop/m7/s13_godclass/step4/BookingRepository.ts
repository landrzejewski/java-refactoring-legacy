import type { Booking } from './Booking.js';
import { LegacyDb } from './LegacyDb.js';

/**
 * Krok 3: jedyny właściciel dostępu do rezerwacji. Pod spodem nadal globalna mapa LegacyDb
 * (zachowana kolejność wstawiania i współdzielenie) - zmiana magazynu to osobny krok.
 * Eksportowany tylko na potrzeby klas kroku (w Javie klasa pakietowa).
 */
export class BookingRepository {
  nextId(): string {
    return 'B' + (LegacyDb.sequence++);
  }

  save(booking: Booking): void {
    LegacyDb.BOOKINGS.set(booking.id, booking);
  }

  /** Zwraca undefined, gdy rezerwacji nie ma - jak LegacyDb.BOOKINGS.get(). */
  find(id: string): Booking | undefined {
    return LegacyDb.BOOKINGS.get(id);
  }

  all(): Iterable<Booking> {
    return LegacyDb.BOOKINGS.values();
  }
}
