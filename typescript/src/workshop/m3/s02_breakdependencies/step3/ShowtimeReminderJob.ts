import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { type Clock, Duration, LocalDateTime, systemClock } from '../../../shared/time.js';
import type { BookingStore } from './BookingStore.js';
import { LegacyDatabase } from './LegacyDatabase.js';
import { ReminderMailer } from './ReminderMailer.js';

/**
 * Krok 3: Subclass and Override Method - wywołanie statycznego mailera przeniesione
 * do metody chronionej sendReminder(). Test nadpisuje ją w podklasie (seam) i po raz
 * pierwszy widzi wysłane przypomnienia. Klasa ma teraz punkt rozszerzenia przez
 * dziedziczenie (protected) - to cena tego seamu.
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
        this.sendReminder(booking.email,
          `Przypomnienie: ${booking.title}`,
          `Seans zaczyna sie o ${booking.start.toLocalTime().toString()}`);
        database.markReminded(booking.id);
        sent++;
      }
    }
    return sent;
  }

  protected sendReminder(to: string, subject: string, body: string): void {
    ReminderMailer.send(to, subject, body);
  }
}
