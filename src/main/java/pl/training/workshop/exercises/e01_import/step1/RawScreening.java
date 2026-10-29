package pl.training.workshop.exercises.e01_import.step1;

/** Krok 1: surowe pola jednego wiersza, zanim przejdą wspólną walidację (bez względu na format pliku). */
public record RawScreening(String title, String format, String hall, String start) {
}
