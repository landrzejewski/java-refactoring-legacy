import { Duration, LocalDateTime } from '../../../shared/time.js';
import { LegacyDatabase } from './LegacyDatabase.js';
import { ReminderMailer } from './ReminderMailer.js';

/**
 * Start: zadanie wysyła przypomnienia na 2 godziny przed seansem.
 * Zapach: wszystkie zależności są "zaszyte" w środku - new LegacyDatabase(),
 * LocalDateTime.now() i statyczny ReminderMailer. Tej klasy nie da się uruchomić w teście.
 */
export class ShowtimeReminderJob {
  run(): number {
    const database = new LegacyDatabase();
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
