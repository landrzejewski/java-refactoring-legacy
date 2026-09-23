package pl.training.workshop.m3.s16_temporalcoupling;

import java.time.LocalDateTime;

/** Stabilny kontrakt sceny - seans. */
public record Screening(String title, String format, LocalDateTime start) {
}
