package pl.training.workshop.m7.s01_breakdependencies.step2;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/**
 * Krok 2: Parameterize Constructor z java.time.Clock - czas przestaje być ukrytym wejściem.
 * Produkcja nadal używa zegara systemowego; zegar czytamy w tym samym miejscu co wcześniej.
 */
public final class ShowtimeReminderJob {
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
