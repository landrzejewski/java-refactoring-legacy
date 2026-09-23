package pl.training.workshop.m8.s02_stranglerfig;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Wspólna baza rezerwacji. W trakcie duszenia stary i nowy kod korzystają z tych samych danych,
 * dlatego raport legacy widzi rezerwacje przyjęte już przez nowy moduł.
 */
public final class BookingLedger {
    private final List<Booking> bookings = new ArrayList<>();
    private int sequence = 1;

    public String nextId() {
        return "B" + sequence++;
    }

    public void add(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> all() {
        return List.copyOf(bookings);
    }

    /** Wiersz bazy: wartość biletów i opłaty rezerwacyjne osobno. */
    public record Booking(String id, String email, String title, int tickets,
            Money ticketsValue, Money fees) {
    }
}
