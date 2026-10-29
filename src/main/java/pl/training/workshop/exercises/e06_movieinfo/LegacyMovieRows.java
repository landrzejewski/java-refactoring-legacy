package pl.training.workshop.exercises.e06_movieinfo;

import java.util.Map;

/**
 * Stara baza kina (nie zmieniamy): tabela MOVIES ze swoimi konwencjami. Ocena jest liczbą całkowitą
 * pomnożoną przez 10 (80 = 8.0), czas w minutach, kod wieku to tekst "0", "7", "12", "16".
 * Nieznany identyfikator to {@code null}. Każde wywołanie liczy się jak zapytanie do bazy.
 */
public final class LegacyMovieRows {
    private static final Map<String, LegacyMovieRow> ROWS = Map.of(
            "M-001", new LegacyMovieRow("Diuna", 2021, 155, 80, "12"),
            "M-002", new LegacyMovieRow("Kraina Lodu II", 2019, 103, 68, "7"),
            "M-003", new LegacyMovieRow("Amator", 1979, 117, 78, "0"),
            "M-004", new LegacyMovieRow("Oppenheimer", 2023, 180, 83, "16"),
            "M-005", new LegacyMovieRow("Seans testowy", 2026, 5, 50, "99"));

    private int calls;

    public LegacyMovieRow find(String id) {
        calls++;
        return ROWS.get(id);
    }

    /** Liczba zapytań do starej bazy. */
    public int calls() {
        return calls;
    }
}
