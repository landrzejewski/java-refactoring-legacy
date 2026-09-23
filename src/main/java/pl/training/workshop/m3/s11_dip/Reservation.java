package pl.training.workshop.m3.s11_dip;

import java.time.LocalDateTime;

/** Stabilny kontrakt sceny - rezerwacja do potwierdzenia. */
public record Reservation(String email, String title, LocalDateTime start, int seats) {
}
