package pl.training.workshop.m7.s01_breakdependencies.step4;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/**
 * Krok 4 (rozwiązanie): seam z dziedziczenia zamieniony na zależność konstruktora
 * (ReminderSender). Pod ochroną testu z kroku 3 klasa znów jest final, a produkcja
 * w domyślnym konstruktorze nadal składa LegacyDatabase, zegar systemowy i ReminderMailer.
 */
public final class ShowtimeReminderJob {
    private final Supplier<BookingStore> stores;
    private final Clock clock;
    private final ReminderSender sender;

    public ShowtimeReminderJob() {
        this(LegacyDatabase::new, Clock.systemDefaultZone(), ReminderMailer::send);
    }

    public ShowtimeReminderJob(Supplier<BookingStore> stores, Clock clock, ReminderSender sender) {
        this.stores = Objects.requireNonNull(stores, "stores");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.sender = Objects.requireNonNull(sender, "sender");
    }

    public int run() {
        BookingStore database = stores.get();
        LocalDateTime now = LocalDateTime.now(clock);
        int sent = 0;
        for (PaidBooking booking : database.paidBookings()) {
            long minutes = Duration.between(now, booking.start()).toMinutes();
            if (!booking.reminded() && minutes > 0 && minutes <= 120) {
                sender.send(booking.email(),
                        "Przypomnienie: " + booking.title(),
                        "Seans zaczyna sie o " + booking.start().toLocalTime());
                database.markReminded(booking.id());
                sent++;
            }
        }
        return sent;
    }
}
