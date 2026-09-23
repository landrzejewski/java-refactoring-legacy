package pl.training.workshop.m4.s07_movemethod.start;

import java.util.ArrayList;
import java.util.List;

/**
 * Start: wydruk rezerwacji. screeningLine i remainingSeats używają WYŁĄCZNIE danych Screening
 * (Feature Envy). print koordynuje Booking i Screening - ona zostaje tutaj.
 */
public final class BookingPrinter {
    public String print(Booking booking) {
        return "Rezerwacja " + booking.id() + "\n"
                + screeningLine(booking.screening()) + "\n"
                + "Miejsce: " + booking.seat() + "\n"
                + "Pozostale wolne: " + remainingSeats(booking.screening(), booking.seat()) + "\n";
    }

    private String screeningLine(Screening s) {
        String format = switch (s.format()) {
            case 3 -> "IMAX";
            case 2 -> "3D";
            default -> "2D";
        };
        return s.title() + " (" + format + "), sala " + s.hall() + ", "
                + s.start().toLocalDate() + " " + s.start().toLocalTime();
    }

    private List<Integer> remainingSeats(Screening s, Integer seat) {
        List<Integer> free = new ArrayList<>(s.freeSeats());
        free.remove(seat);
        return free;
    }
}
