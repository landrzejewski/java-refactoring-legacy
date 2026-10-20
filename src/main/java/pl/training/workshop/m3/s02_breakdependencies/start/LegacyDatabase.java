package pl.training.workshop.m3.s02_breakdependencies.start;

import java.util.List;

import pl.training.workshop.m3.s02_breakdependencies.PaidBooking;

/** Produkcyjna baza. Konstruktor otwiera połączenie - poza serwerownią kina to się nie uda. */
public final class LegacyDatabase {
    public LegacyDatabase() {
        throw new IllegalStateException("brak polaczenia z jdbc:oracle:thin:@prod-db:1521/CINEMA");
    }

    public List<PaidBooking> paidBookings() {
        return List.of();
    }

    public void markReminded(String bookingId) {
        // UPDATE bookings SET reminded = 1 WHERE id = ?
    }
}
