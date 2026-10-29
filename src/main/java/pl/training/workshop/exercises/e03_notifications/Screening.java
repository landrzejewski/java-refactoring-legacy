package pl.training.workshop.exercises.e03_notifications;

/** Stabilny kontrakt sceny: seans w repertuarze (termin jako tekst, np. {@code 2026-10-30 18:00}). */
public record Screening(String id, String title, String start, String hall) {
}
