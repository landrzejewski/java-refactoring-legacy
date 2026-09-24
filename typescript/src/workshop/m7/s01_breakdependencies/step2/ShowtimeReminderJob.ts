import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { type Clock, Duration, LocalDateTime, systemClock } from '../../../shared/time.js';
import type { BookingStore } from './BookingStore.js';
import { LegacyDatabase } from './LegacyDatabase.js';
import { ReminderMailer } from './ReminderMailer.js';

/**
 * Krok 2: Parameterize Constructor z Clock - czas przestaje być ukrytym wejściem.
 * Produkcja nadal używa zegara systemowego; zegar czytamy w tym samym miejscu co wcześniej.
 */
export class ShowtimeReminderJob {
  private readonly stores: () => BookingStore;
  private readonly clock: Clock;

  constructor(stores: () => BookingStore = () => new LegacyDatabase(), clock: Clock = systemClock) {
    this.stores = requireNonNull(stores, 'stores');
    this.clock = requireNonNull(clock, 'clock');
  }

  run(): number {
    const database = this.stores();
    const now = LocalDateTime.now(this.clock);
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
