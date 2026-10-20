import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { type Clock, Duration, LocalDateTime, systemClock } from '../../../shared/time.js';
import type { BookingStore } from './BookingStore.js';
import { LegacyDatabase } from './LegacyDatabase.js';
import { ReminderMailer } from './ReminderMailer.js';
import type { ReminderSender } from './ReminderSender.js';

/**
 * Krok 4 (rozwiązanie): seam z dziedziczenia zamieniony na zależność konstruktora
 * (ReminderSender). Pod ochroną testu z kroku 3 znika metoda protected, a produkcja
 * (parametry domyślne) nadal składa LegacyDatabase, zegar systemowy i ReminderMailer.
 */
export class ShowtimeReminderJob {
  private readonly stores: () => BookingStore;
  private readonly clock: Clock;
  private readonly sender: ReminderSender;

  constructor(
    stores: () => BookingStore = () => new LegacyDatabase(),
    clock: Clock = systemClock,
    sender: ReminderSender = (to, subject, body) => ReminderMailer.send(to, subject, body),
  ) {
    this.stores = requireNonNull(stores, 'stores');
    this.clock = requireNonNull(clock, 'clock');
    this.sender = requireNonNull(sender, 'sender');
  }

  run(): number {
    const database = this.stores();
    const now = LocalDateTime.now(this.clock);
    let sent = 0;
    for (const booking of database.paidBookings()) {
      const minutes = Duration.between(now, booking.start).toMinutes();
      if (!booking.reminded && minutes > 0 && minutes <= 120) {
        this.sender(booking.email,
          `Przypomnienie: ${booking.title}`,
          `Seans zaczyna sie o ${booking.start.toLocalTime().toString()}`);
        database.markReminded(booking.id);
        sent++;
      }
    }
    return sent;
  }
}
