package pl.training.workshop.m7.s01_breakdependencies.step3;

import java.util.List;

import pl.training.workshop.m7.s01_breakdependencies.PaidBooking;

/** Produkcyjna baza. Konstruktor otwiera połączenie - poza serwerownią kina to się nie uda. */
public final class LegacyDatabase implements BookingStore {
    public LegacyDatabase() {
        throw new IllegalStateException("brak polaczenia z jdbc:oracle:thin:@prod-db:1521/CINEMA");
    }

    @Override
    public List<PaidBooking> paidBookings() {
        return List.of();
    }

    @Override
    public void markReminded(String bookingId) {
        // UPDATE bookings SET reminded = 1 WHERE id = ?
    }
}
