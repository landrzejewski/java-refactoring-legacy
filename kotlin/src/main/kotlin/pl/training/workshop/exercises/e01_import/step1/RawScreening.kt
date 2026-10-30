package pl.training.workshop.exercises.e01_import.step1

/** Krok 1: surowe pola jednego wiersza, zanim przejdą wspólną walidację (bez względu na format pliku). */
data class RawScreening(val title: String, val format: String, val hall: String, val start: String)
