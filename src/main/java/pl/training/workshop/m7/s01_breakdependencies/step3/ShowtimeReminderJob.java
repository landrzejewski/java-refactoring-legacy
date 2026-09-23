package pl.training.workshop.m7.s01_breakdependencies.step3;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/**
 * Krok 3: Subclass and Override Method - wywołanie statycznego mailera przeniesione
 * do metody chronionej sendReminder(). Test nadpisuje ją w podklasie (seam) i po raz
 * pierwszy widzi wysłane przypomnienia. Klasa przestała być final - to cena tego seamu.
 */
public class ShowtimeReminderJob {
    private final Supplier<BookingStore> stores;
    private final Clock clock;

    public ShowtimeReminderJob() {
        this(LegacyDatabase::new, Clock.systemDefaultZone());
    }

    public ShowtimeReminderJob(Supplier<BookingStore> stores, Clock clock) {
        this.stores = Objects.requireNonNull(stores, "stores");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public int run() {
        BookingStore database = stores.get();
        LocalDateTime now = LocalDateTime.now(clock);
        int sent = 0;
        for (PaidBooking booking : database.paidBookings()) {
            long minutes = Duration.between(now, booking.start()).toMinutes();
            if (!booking.reminded() && minutes > 0 && minutes <= 120) {
                sendReminder(booking.email(),
                        "Przypomnienie: " + booking.title(),
                        "Seans zaczyna sie o " + booking.start().toLocalTime());
                database.markReminded(booking.id());
                sent++;
            }
        }
        return sent;
    }

    protected void sendReminder(String to, String subject, String body) {
        ReminderMailer.send(to, subject, body);
    }
}
