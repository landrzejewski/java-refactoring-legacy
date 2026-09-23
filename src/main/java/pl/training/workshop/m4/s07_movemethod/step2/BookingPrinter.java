package pl.training.workshop.m4.s07_movemethod.step2;

/**
 * Krok 2 (rozwiązanie): Move Method {@code remainingSeats}
 * -> {@code Screening.freeSeatsWithout(Integer)}. BookingPrinter zostaje koordynatorem wydruku:
 * składa tekst z tego, co wiedzą Booking i Screening.
 */
public final class BookingPrinter {
    public String print(Booking booking) {
        return "Rezerwacja " + booking.id() + "\n"
                + booking.screening().headline() + "\n"
                + "Miejsce: " + booking.seat() + "\n"
                + "Pozostale wolne: " + booking.screening().freeSeatsWithout(booking.seat()) + "\n";
    }
}
