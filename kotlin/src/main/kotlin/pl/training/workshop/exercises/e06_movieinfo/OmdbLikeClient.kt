package pl.training.workshop.exercises.e06_movieinfo

/**
 * Obca biblioteka (nie zmieniamy): klient internetowej bazy filmów w stylu OMDb. Odpowiada surową
 * mapą pól tekstowych: czas trwania jako "155 min", ocena jako "8.0/10", rok jako tekst, kategoria
 * wiekowa w kodach amerykańskich (G, PG, PG-13, R, Not Rated). Nieznany film to odpowiedź
 * z polem Response=False, jak w prawdziwym API. Każde wywołanie liczy się jak zapytanie sieciowe.
 */
class OmdbLikeClient {
    private var calls = 0

    fun byId(imdbId: String): Map<String, String> {
        calls++
        return MOVIES[imdbId] ?: mapOf("Response" to "False", "Error" to "Movie not found!")
    }

    /** Liczba zapytań do zewnętrznego API - w produkcji każde kosztuje czas i limit dzienny. */
    fun calls(): Int = calls

    private companion object {
        val MOVIES = mapOf(
            "tt1160419" to mapOf(
                "Title" to "Dune", "Year" to "2021", "Runtime" to "155 min",
                "imdbRating" to "8.0/10", "Rated" to "PG-13", "Response" to "True"
            ),
            "tt4520988" to mapOf(
                "Title" to "Frozen II", "Year" to "2019", "Runtime" to "103 min",
                "imdbRating" to "6.8/10", "Rated" to "PG", "Response" to "True"
            ),
            "tt15398776" to mapOf(
                "Title" to "Oppenheimer", "Year" to "2023", "Runtime" to "180 min",
                "imdbRating" to "8.3/10", "Rated" to "R", "Response" to "True"
            ),
            "tt0077269" to mapOf(
                "Title" to "Amator", "Year" to "1979", "Runtime" to "117 min",
                "imdbRating" to "7.8/10", "Rated" to "Not Rated", "Response" to "True"
            )
        )
    }
}
