package pl.training.workshop.m7.s13_godclass.step4;

import java.util.Collection;

/**
 * Krok 3: jedyny właściciel dostępu do rezerwacji. Pod spodem nadal globalna mapa LegacyDb
 * (zachowana kolejność wstawiania i współdzielenie) - zmiana magazynu to osobny krok.
 */
final class BookingRepository {
    String nextId() {
        return "B" + (LegacyDb.sequence++);
    }

    void save(Booking booking) {
        LegacyDb.BOOKINGS.put(booking.id(), booking);
    }

    /** Zwraca null, gdy rezerwacji nie ma - jak LegacyDb.BOOKINGS.get(); Optional to osobna zmiana. */
    Booking find(String id) {
        return LegacyDb.BOOKINGS.get(id);
    }

    Collection<Booking> all() {
        return LegacyDb.BOOKINGS.values();
    }
}
