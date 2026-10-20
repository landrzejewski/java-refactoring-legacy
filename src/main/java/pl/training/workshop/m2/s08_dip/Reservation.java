package pl.training.workshop.m2.s08_dip;

import java.time.LocalDateTime;

/** Stabilny kontrakt sceny - rezerwacja do potwierdzenia. */
public record Reservation(String email, String title, LocalDateTime start, int seats) {
}
