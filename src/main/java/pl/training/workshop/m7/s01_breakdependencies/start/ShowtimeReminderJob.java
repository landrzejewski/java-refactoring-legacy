package pl.training.workshop.m7.s01_breakdependencies.start;

import java.time.Duration;
import java.time.LocalDateTime;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/**
 * Start: zadanie wysyła przypomnienia na 2 godziny przed seansem.
 * Zapach: wszystkie zależności są "zaszyte" w środku - new LegacyDatabase(),
 * LocalDateTime.now() i statyczny ReminderMailer. Tej klasy nie da się uruchomić w teście.
 */
public final class ShowtimeReminderJob {
    public int run() {
        LegacyDatabase database = new LegacyDatabase();
        LocalDateTime now = LocalDateTime.now();
        int sent = 0;
        for (PaidBooking booking : database.paidBookings()) {
            long minutes = Duration.between(now, booking.start()).toMinutes();
            if (!booking.reminded() && minutes > 0 && minutes <= 120) {
                ReminderMailer.send(booking.email(),
                        "Przypomnienie: " + booking.title(),
                        "Seans zaczyna sie o " + booking.start().toLocalTime());
                database.markReminded(booking.id());
                sent++;
            }
        }
        return sent;
    }
}
