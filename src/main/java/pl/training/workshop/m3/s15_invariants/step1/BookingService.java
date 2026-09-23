package pl.training.workshop.m3.s15_invariants.step1;

import java.math.BigDecimal;

/** Krok 1: walidacja nadal tutaj, tworzenie przez konstruktor rekordu. */
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
        Reservation reservation = new Reservation(email, seats, total);
        return "zarezerwowano: " + reservation.email() + ", miejsc " + reservation.seats()
                + ", kwota " + reservation.total();
    }
}
