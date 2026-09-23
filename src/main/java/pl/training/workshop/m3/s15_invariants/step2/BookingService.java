package pl.training.workshop.m3.s15_invariants.step2;

import java.math.BigDecimal;

/** Krok 2: serwis nie powtarza reguł modelu - tylko koordynuje. */
public final class BookingService {
    public String book(String email, int seats, BigDecimal total) {
        Reservation reservation = new Reservation(email, seats, total);
        return "zarezerwowano: " + reservation.email() + ", miejsc " + reservation.seats()
                + ", kwota " + reservation.total();
    }
}
