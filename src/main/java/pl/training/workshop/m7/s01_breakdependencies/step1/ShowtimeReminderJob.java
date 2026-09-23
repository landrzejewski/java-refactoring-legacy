package pl.training.workshop.m7.s01_breakdependencies.step1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/**
 * Krok 1: Extract Interface + Parameterize Constructor - baza schowana za BookingStore
 * i przekazywana z zewnątrz. Fabryka (Supplier) zachowuje czas życia: połączenie nadal
 * powstaje przy każdym run(), a nie przy tworzeniu zadania.
 */
public final class ShowtimeReminderJob {
    private final Supplier<BookingStore> stores;

    public ShowtimeReminderJob() {
        this(LegacyDatabase::new);
    }

    public ShowtimeReminderJob(Supplier<BookingStore> stores) {
        this.stores = Objects.requireNonNull(stores, "stores");
    }

    public int run() {
        BookingStore database = stores.get();
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
