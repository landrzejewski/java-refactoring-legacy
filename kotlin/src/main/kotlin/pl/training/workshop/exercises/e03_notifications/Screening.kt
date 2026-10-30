package pl.training.workshop.exercises.e03_notifications

/** Stabilny kontrakt sceny: seans w repertuarze (termin jako tekst, np. `2026-10-30 18:00`). */
data class Screening(val id: String, val title: String, val start: String, val hall: String)
