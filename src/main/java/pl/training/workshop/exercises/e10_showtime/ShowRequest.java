package pl.training.workshop.exercises.e10_showtime;

/** Stabilny kontrakt ćwiczenia: seans do uruchomienia w sali - identyfikator (dla bramki i raportu) i tytuł (dla projektora). */
public record ShowRequest(String screeningId, String title) {
}
