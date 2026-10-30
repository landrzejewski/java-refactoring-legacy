package pl.training.workshop.exercises.e06_movieinfo

/**
 * Stara baza kina (nie zmieniamy): tabela MOVIES ze swoimi konwencjami. Ocena jest liczbą całkowitą
 * pomnożoną przez 10 (80 = 8.0), czas w minutach, kod wieku to tekst "0", "7", "12", "16".
 * Nieznany identyfikator to `null`. Każde wywołanie liczy się jak zapytanie do bazy.
 */
class LegacyMovieRows {
    private var calls = 0

    fun find(id: String): LegacyMovieRow? {
        calls++
        return ROWS[id]
    }

    /** Liczba zapytań do starej bazy. */
    fun calls(): Int = calls

    private companion object {
        val ROWS = mapOf(
            "M-001" to LegacyMovieRow("Diuna", 2021, 155, 80, "12"),
            "M-002" to LegacyMovieRow("Kraina Lodu II", 2019, 103, 68, "7"),
            "M-003" to LegacyMovieRow("Amator", 1979, 117, 78, "0"),
            "M-004" to LegacyMovieRow("Oppenheimer", 2023, 180, 83, "16"),
            "M-005" to LegacyMovieRow("Seans testowy", 2026, 5, 50, "99")
        )
    }
}
