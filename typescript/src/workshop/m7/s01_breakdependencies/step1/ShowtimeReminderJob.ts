import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Duration, LocalDateTime } from '../../../shared/time.js';
import type { BookingStore } from './BookingStore.js';
import { LegacyDatabase } from './LegacyDatabase.js';
import { ReminderMailer } from './ReminderMailer.js';

/**
 * Krok 1: Extract Interface + Parameterize Constructor - baza schowana za BookingStore
 * i przekazywana z zewnątrz. Fabryka (() => BookingStore) zachowuje czas życia: połączenie
 * nadal powstaje przy każdym run(), a nie przy tworzeniu zadania.
 */
export class ShowtimeReminderJob {
  private readonly stores: () => BookingStore;

  constructor(stores: () => BookingStore = () => new LegacyDatabase()) {
    this.stores = requireNonNull(stores, 'stores');
  }

  run(): number {
    const database = this.stores();
    const now = LocalDateTime.now();
    let sent = 0;
    for (const booking of database.paidBookings()) {
      const minutes = Duration.between(now, booking.start).toMinutes();
      if (!booking.reminded && minutes > 0 && minutes <= 120) {
        ReminderMailer.send(booking.email,
          `Przypomnienie: ${booking.title}`,
          `Seans zaczyna sie o ${booking.start.toLocalTime().toString()}`);
        database.markReminded(booking.id);
        sent++;
      }
    }
    return sent;
  }
}
