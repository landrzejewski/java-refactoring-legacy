package pl.training.workshop.exercises.e06_movieinfo.step1

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/**
 * Krok 1: port w języku kina - preferowany interfejs katalogu filmów. Mówi o minutach, ocenie 0-10
 * i kategorii kina, nie o "155 min", "8.0/10" ani kodach tabeli MOVIES.
 */
fun interface MovieCatalog {
    /** Dane filmu albo [IllegalArgumentException] "unknown movie: id" - niezależnie od źródła. */
    fun find(id: String): MovieDetails
}
