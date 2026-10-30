package pl.training.workshop.exercises.e06_movieinfo.step3

/** Krok 3: Extract Interface - adaptery zależą od kontraktu tabeli, nie od jedynej instancji. */
fun interface AgeRatings {
    /** Kategoria kina dla kodu z obcego systemu; nieznany kod to "brak". */
    fun categoryFor(system: String, code: String): String
}
