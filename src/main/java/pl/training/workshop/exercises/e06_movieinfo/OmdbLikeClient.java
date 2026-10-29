package pl.training.workshop.exercises.e06_movieinfo;

import java.util.Map;

/**
 * Obca biblioteka (nie zmieniamy): klient internetowej bazy filmów w stylu OMDb. Odpowiada surową
 * mapą pól tekstowych: czas trwania jako "155 min", ocena jako "8.0/10", rok jako tekst, kategoria
 * wiekowa w kodach amerykańskich (G, PG, PG-13, R, Not Rated). Nieznany film to odpowiedź
 * z polem Response=False, jak w prawdziwym API. Każde wywołanie liczy się jak zapytanie sieciowe.
 */
public final class OmdbLikeClient {
    private static final Map<String, Map<String, String>> MOVIES = Map.of(
            "tt1160419", Map.of("Title", "Dune", "Year", "2021", "Runtime", "155 min",
                    "imdbRating", "8.0/10", "Rated", "PG-13", "Response", "True"),
            "tt4520988", Map.of("Title", "Frozen II", "Year", "2019", "Runtime", "103 min",
                    "imdbRating", "6.8/10", "Rated", "PG", "Response", "True"),
            "tt15398776", Map.of("Title", "Oppenheimer", "Year", "2023", "Runtime", "180 min",
                    "imdbRating", "8.3/10", "Rated", "R", "Response", "True"),
            "tt0077269", Map.of("Title", "Amator", "Year", "1979", "Runtime", "117 min",
                    "imdbRating", "7.8/10", "Rated", "Not Rated", "Response", "True"));

    private int calls;

    public Map<String, String> byId(String imdbId) {
        calls++;
        Map<String, String> movie = MOVIES.get(imdbId);
        return movie != null ? movie : Map.of("Response", "False", "Error", "Movie not found!");
    }

    /** Liczba zapytań do zewnętrznego API - w produkcji każde kosztuje czas i limit dzienny. */
    public int calls() {
        return calls;
    }
}
