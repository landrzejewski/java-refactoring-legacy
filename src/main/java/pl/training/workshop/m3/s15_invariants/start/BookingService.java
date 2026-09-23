package pl.training.workshop.m3.s15_invariants.start;

import java.math.BigDecimal;

/** Start: jedyne miejsce, które waliduje - bo akurat ktoś pamiętał. */
public final class BookingService {
    public String book(String email, int seats, BigDecimal total) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("niepoprawny email: " + email);
        }
        if (seats < 1) {
            throw new IllegalArgumentException("liczba miejsc musi byc dodatnia: " + seats);
        }
        if (total.signum() < 0) {
            throw new IllegalArgumentException("kwota nie moze byc ujemna: " + total);
        }
        Reservation reservation = new Reservation();
        reservation.setEmail(email);
        reservation.setSeats(seats);
        reservation.setTotal(total);
        return "zarezerwowano: " + reservation.getEmail() + ", miejsc " + reservation.getSeats()
                + ", kwota " + reservation.getTotal();
    }
}
