package pl.training.workshop.m4.s07_movemethod.step1;

import java.util.ArrayList;
import java.util.List;

/**
 * Krok 1: Move Method {@code screeningLine} -> {@code Screening.headline()}.
 * Metoda używała tylko danych seansu, więc właścicielem jest Screening.
 * Wywołanie po przeniesieniu czyta się {@code booking.screening().headline()}.
 */
public final class BookingPrinter {
    public String print(Booking booking) {
        return "Rezerwacja " + booking.id() + "\n"
                + booking.screening().headline() + "\n"
                + "Miejsce: " + booking.seat() + "\n"
                + "Pozostale wolne: " + remainingSeats(booking.screening(), booking.seat()) + "\n";
    }

    private List<Integer> remainingSeats(Screening s, Integer seat) {
        List<Integer> free = new ArrayList<>(s.freeSeats());
        free.remove(seat);
        return free;
    }
}
