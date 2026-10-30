package pl.training.workshop.exercises.e06_movieinfo.step3

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails

/** Krok 3: port katalogu bez zmian. */
fun interface MovieCatalog {
    /** Dane filmu albo [IllegalArgumentException] "unknown movie: id" - niezależnie od źródła. */
    fun find(id: String): MovieDetails
}
