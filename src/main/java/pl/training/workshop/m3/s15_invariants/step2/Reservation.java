package pl.training.workshop.m3.s15_invariants.step2;

import java.math.BigDecimal;

/**
 * Krok 2 (rozwiązanie): Move Method - strażnicy przeniesieni do kompaktowego konstruktora.
 * Inwarianty mają jednego właściciela: nie da się utworzyć rezerwacji w złym stanie,
 * niezależnie od ścieżki (kasa, online, import).
 */
public record Reservation(String email, int seats, BigDecimal total) {
    public Reservation {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("niepoprawny email: " + email);
        }
        if (seats < 1) {
            throw new IllegalArgumentException("liczba miejsc musi byc dodatnia: " + seats);
        }
        if (total.signum() < 0) {
            throw new IllegalArgumentException("kwota nie moze byc ujemna: " + total);
        }
    }
}
